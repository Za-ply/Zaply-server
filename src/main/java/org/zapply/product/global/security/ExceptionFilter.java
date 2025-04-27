package org.zapply.product.global.security;

import ch.qos.logback.core.status.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zapply.product.global.apiPayload.exception.CoreException;
import org.zapply.product.global.apiPayload.exception.GlobalErrorType;
import org.zapply.product.global.apiPayload.response.ApiResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class ExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CoreException e) { //Todo: Error handling (Core Exception Handling)
            setResponse(response);
        }
    }


    private void setResponse(HttpServletResponse response) throws IOException{
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(GlobalErrorType.FORBIDDEN.getStatus().value());

        ExecutionException error = new ExecutionException(GlobalErrorType.FORBIDDEN.getMessage(), new Throwable());
        String errorJson = objectMapper.writeValueAsString(error);

        response.getWriter().write(errorJson);
    }
}