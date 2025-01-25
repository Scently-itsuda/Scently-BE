package com.itsuda.perfume.security;

public class Constants {
    public static final String USER_ID_CLAIM_NAME = "uid";
    public static final String USER_ROLE_CLAIM_NAME = "rol";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String[] NO_NEED_AUTH_URLS = {
            "/auth/**",
            "/oauth2/authorization/kakao", "/login/oauth2/code/kakao",
            "/oauth2/authorization/naver", "/login/oauth2/code/naver",
            "/oauth2/authorization/google", "/login/oauth2/code/google",
            "/api/auth/reissue",
            "/swagger-ui/**", "/api-docs/**", "/swagger-resources/**", "/webjars/**", "/swagger-ui.html"
    };
}
