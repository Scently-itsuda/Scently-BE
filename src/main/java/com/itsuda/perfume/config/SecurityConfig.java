package com.itsuda.perfume.config;

import com.itsuda.perfume.security.handler.OAuth2LoginFailureHandler;
import com.itsuda.perfume.security.handler.OAuth2LoginSuccessHandler;
import com.itsuda.perfume.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

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
                .getOrBuild();
    }
}
