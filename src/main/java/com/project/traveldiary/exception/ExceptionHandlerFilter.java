package com.project.traveldiary.exception;

import static com.project.traveldiary.type.ErrorCode.INVALID_TOKEN;
import static com.project.traveldiary.type.ErrorCode.NEED_LOGIN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.traveldiary.type.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | MalformedJwtException e) {
            setResponseError(response, INVALID_TOKEN);
        } catch (AuthenticationException e) {
            setResponseError(response, NEED_LOGIN);
        }
    }

    private void setResponseError(HttpServletResponse response, ErrorCode errorCode)
        throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");

        response.getWriter().write(
            objectMapper.writeValueAsString(ResponseError.builder()
                .errorCode(errorCode)
                .message(errorCode.getMessage())
                .build())
        );
    }
}
