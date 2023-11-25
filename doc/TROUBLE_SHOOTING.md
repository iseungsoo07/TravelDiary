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