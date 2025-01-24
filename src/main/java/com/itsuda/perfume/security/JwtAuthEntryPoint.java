package com.itsuda.perfume.security;

import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtAuthEntryPoint extends ErrorResponse implements AuthenticationEntryPoint {

    @Override
    public void commence(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {
        final ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");

        if (errorCode == null) {
            setErrorResponse(response, ErrorCode.NOT_END_POINT);
            return;
        }

        setErrorResponse(response, errorCode);
    }
}
