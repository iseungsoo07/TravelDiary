# Trouble Shooting

---
프로젝트를 진행하면서 발생한 문제점들과 해결법을 서술한다.

### Controller가 아닌 Filter에서 발생한 예외 핸들링

@ExceptionHandler 어노테이션을 사용해, GlobalExceptionHandler 클래스에 ExpiredJwtException 예외를 처리하는 로직을 작성했었다.

```
public class GlobalExceptionHandler { 

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseError handleExpiredJwtException(ExpiredJwtException e) {
        return new ResponseError(INVALID_TOKEN, INVALID_TOKEN.getMessage());
    }
}
```

그러나 스프링 필터는 서블릿에 도달하기 전에 처리가 되기 때문에 필터에서 발생한 예외는 GlobalExceptionHandler에 도달할 수 없었다.
따라서 필터에서 발생한 예외를 처리하는 별도의 로직이 필요했고, JwtAuthenticationFilter 이전에 ExceptionHandlerFilter를 추가해 필터에서 발생한 예외를 처리할 수 있도록 했다.
```
@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            setResponseError(response);
        }
    }

    private void setResponseError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");

        response.getWriter().write(
            // json 형태로 값을 담아준다.
            objectMapper.writeValueAsString(ResponseError.builder()
                .errorCode(INVALID_TOKEN)
                .message(INVALID_TOKEN.getMessage())
                .build())
        );
    }
}
```

### MySQL JSON 타입 필드를 엔티티와 매핑
MySQL에는 JSON 형태를 저장할 수 있는 JSON 타입이 있다.<br>
해시태그 테이블을 따로 구성할 수도 있겠지만, 별도의 테이블로 나눌 경우 join을 해서 보여줘야 한다거나, 별도의 select가 발생할 수 있기 때문에
이번 프로젝트에서는 해시태그 테이블을 따로 구성하지 않고 JSON 형태로 저장해서 일기가 가진 모든 해시태그를 한 번에 보여지도록 구성하기로 했다.
<br>
JPA에서 JSON 형태의 자료를 DB 컬럼에 삽입하기 위해서는 별도의 의존성이 필요하다.
JPA의 구현체인 hibernate에만 부가적으로 사용 가능한 기능이기 때문에 hibernate를 사용하는 경우에만 적용할 수 있는 외부 라이브러리이다.
```
implementation 'com.vladmihalcea:hibernate-types-52:2.16.2'
```
위의 의존성을 추가한 뒤 JSON을 사용하는 Entity를 만들때 추가적인 어노테이션을 사용해주면 된다.
```
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class Diary {
    @Id
    @GeneratedValue(strategy = "GenerationType.IDENTITY")
    private Long id;
    
    @Type(type = "json")
    @Column(columnDefinition = "longtext")
    private List<String> hashtags = new ArrayList<>();
}    
```
@TypeDef 어노테이션을 사용해 이름과 클래스를 지정해주고, JSON 타입으로 사용할 필드에 @Type 어노테이션을 사용해준다.
<br>
<del>DB 컬럼 hashtags를 hahstags로 적어서 2시간동안 헤맨거는 안비밀...</del>

### AOP pointcut에 arg 전달
동시성 이슈를 제어하기 위한 redis 분산 락을 사용하는데, 이를 AOP를 이용해서 처리하려고 시도했다.
다른 강의에서 봤던 코드를 참고해서 aop를 작성했는데 아무리 시도해도 lock을 획득하고 해제하는 log가 보이질 않았다.
```
@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class LockAopAspect {

    private final LockManager lockManager;

    @Around("@annotation(com.project.traveldiary.aop.DistributedLock) && args(id)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint, Long id)
        throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedLock lock = signature.getMethod().getAnnotation(DistributedLock.class);

        lockManager.lock(lock.prefix(), String.valueOf(id));

        try {
            return joinPoint.proceed();
        } finally {
            lockManager.unlock(lock.prefix(), String.valueOf(id));
        }
    }
}
```
aroundMethod 내에서 일기의 고유 id 값을 사용하기 위해 args로 id 값을 받아왔다. 

```
@Override
@Transactional
@DistributedLock(prefix = "like_diary")
public DiaryLikeResponse likeDiary(Long id, String userId) {
    User user = userRepository.findByUserId(userId)
        .orElseThrow(() -> new UserException(NOT_FOUND_USER));

    Diary diary = diaryRepository.findById(id)
        .orElseThrow(() -> new DiaryException(NOT_FOUND_DIARY));

    if (likesRepository.existsByUserAndDiary(user, diary)) {
        throw new LikeException(ALREADY_LIKE_DIARY);
    }

    Likes savedLike = likesRepository.save(Likes.builder()
        .user(user)
        .diary(diary)
        .build());

    diary.increaseLikeCount();

    diaryRepository.save(diary);

    String fromUser = savedLike.getUser().getNickname();
    String toUser = savedLike.getDiary().getUser().getNickname();

    return DiaryLikeResponse.builder()
        .userId(fromUser)
        .writer(toUser)
        .build();
}
```
likeDiary 서비스 메소드는 위와 같이 작성했었다. 하지만 lock을 획득하는 과정은 계속 시도해도 수행되지 않았고
test 코드를 작성하면서 무엇이 잘못된 지 알게 되었다. 문제는 @Around 어노테이션의 args에 메소드에서 전달받는 인자를 모두 작성해줘야 하는 것이었다.
```
@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class LockAopAspect {

    private final LockManager lockManager;

    @Around("@annotation(com.project.traveldiary.aop.DistributedLock) && args(id, userId)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint, Long id, String userId)
        throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedLock lock = signature.getMethod().getAnnotation(DistributedLock.class);

        lockManager.lock(lock.prefix(), String.valueOf(id));

        try {
            return joinPoint.proceed();
        } finally {
            lockManager.unlock(lock.prefix(), String.valueOf(id));
        }
    }
}
```
위 처럼 수정하니 정상 동작하는 것을 확인할 수 있었다.

### Elasticsearch
elasticsearch 사용 과정에서 사용자에게 받은 입력값에 공백이 있는 경우 아래와 같은 에러가 발생하는 상황을 마주했다.
![img.png](img.png)
elasticsearch에 대한 충분한 공부가 이루어지지 않은 상황에서 각 필드의 설정을 잘못해서 발생한 상황이었다.
title을 text 타입으로 지정했었는데, keyword로 지정해주었고, native query를 작성할 때 `matchPhrase()` 메소드를 사용해 띄어쓰기도 포함하도록 설정해주었다.
ElasticSearch를 사용하기 위해서는 RDB에서 table의 row로 여겨지는 Document라는 구조를 만들어줘야 하는데
이를 매번 일기의 생성, 수정, 삭제 시 마다 document를 저장하는 메소드를 호출하게 되면 성능적인 저하가 있을거라고 예상했다.
이를 해결하기 위한 별도의 방안이 있을거라고 생각했는데 멘토님께서 꼭 필요한 연산의 경우는 어쩔 수 없다고 하셨다.

