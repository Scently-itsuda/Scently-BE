package com.itsuda.perfume.security.handler;

import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler extends ErrorResponse implements AccessDeniedHandler {

    @Override
    public void handle(final HttpServletRequest request,
                       final HttpServletResponse response,
                       final AccessDeniedException accessDeniedException) throws IOException {
        setErrorResponse(response, ErrorCode.ACCESS_DENIED_ERROR);
    }
}

