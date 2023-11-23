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

