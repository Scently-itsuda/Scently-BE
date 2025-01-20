package com.itsuda.perfume.config;

import com.itsuda.perfume.security.Constants;
import com.itsuda.perfume.security.CustomAuthenticationProvider;
import com.itsuda.perfume.security.JwtAuthEntryPoint;
import com.itsuda.perfume.security.filter.JwtExceptionFilter;
import com.itsuda.perfume.security.filter.JwtFilter;
import com.itsuda.perfume.security.handler.JwtAccessDeniedHandler;
import com.itsuda.perfume.security.handler.OAuth2LoginFailureHandler;
import com.itsuda.perfume.security.handler.OAuth2LoginSuccessHandler;
import com.itsuda.perfume.security.handler.signout.CustomSignOutProcessHandler;
import com.itsuda.perfume.security.handler.signout.CustomSignOutResultHandler;
import com.itsuda.perfume.service.CustomOAuth2UserService;
import com.itsuda.perfume.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtUtil jwtUtil;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CustomSignOutProcessHandler customSignOutProcessHandler;
    private final CustomSignOutResultHandler customSignOutResultHandler;

    @Bean
    protected SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                //csrf(Cross-Site Request Forgery) disable
                .csrf(AbstractHttpConfigurer::disable)
                //Form 로그인 방식 disable
                .formLogin(AbstractHttpConfigurer::disable)
                //HTTP Basic 인증 방식 disable
                .httpBasic(AbstractHttpConfigurer::disable)
                //세션 설정 : STATELESS
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //경로별 인가 작업
                .authorizeHttpRequests(requestMatcherRegistry -> requestMatcherRegistry
                        .requestMatchers(Constants.NO_NEED_AUTH_URLS).permitAll()
                        .anyRequest().authenticated())

                //oauth2 (소셜 로그인)
                .oauth2Login(oauth -> oauth
                        // 커스텀 한 핸들러 등록
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        // 커스텀한 OAuth2UserService 등록
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService)
                        )
                )
                // 로그아웃 설정
                .logout(configurer ->
                        configurer
                                .logoutUrl("/auth/logout")
                                .addLogoutHandler(customSignOutProcessHandler)
                                .logoutSuccessHandler(customSignOutResultHandler)
                                .deleteCookies("JSESSIONID")
                )

                //예외 처리 설정
                .exceptionHandling(configurer ->
                        configurer
                                .authenticationEntryPoint(jwtAuthEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                // JWTFilter, JwtExceptionFilter 추가
                // 필터 위치에 따라 OAuth2 인증을 진행하는 필터보다 JWTFilter가 앞에 존재하는 경우 아래와 같은 오류가 발생할 수 있습니다.
                // 1.재로그인 2. JWT 만료 → 거절 3. OAuth2 로그인 실패 → 재요청 4. 무한 루프
                .addFilterAfter(new JwtFilter(jwtUtil, customAuthenticationProvider), OAuth2LoginAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtFilter.class)

                //SecurityFilterChain 빈을 반환
                .getOrBuild();
    }
}
