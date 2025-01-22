package com.itsuda.perfume.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class UserIdInterceptor implements HandlerInterceptor {
    // preHandler() : 컨트롤러 메서드(핸들러)가 실행되기 전에 실행되는 메서드
    // ArgumentResolver가 사용할 수 있도록 데이터를 준비하는 역할
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("설정");
        log.info(authentication.getName());

        // 추출한 사용자 ID를 request의 attribute로 저장 (키: "USER_ID")
        request.setAttribute("USER_ID", authentication.getName());

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
