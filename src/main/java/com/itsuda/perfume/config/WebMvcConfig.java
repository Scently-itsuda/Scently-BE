package com.itsuda.perfume.config;

import com.itsuda.perfume.intercepter.UserIdArgumentResolver;
import com.itsuda.perfume.intercepter.UserIdInterceptor;
import com.itsuda.perfume.security.Constants;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final UserIdArgumentResolver userIdArgumentResolver;


    // 안드로이드 앱과의 통신에는 CORS가 필요 없지만, 개발 과정에서 Swagger UI를 통해 API를 테스트하고 문서를 확인하기 위해 CORS 설정 추가
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                            "http://scently.kro.kr:8080",
                            "http://scently.kro.kr"
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //기존 resolver 들 등록
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);

        //userId resolver 등록
        resolvers.add(this.userIdArgumentResolver);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        //Interceptor 추가
        registry.addInterceptor(new UserIdInterceptor())
                .addPathPatterns("/**") //적용하는 경로 추가 (모든 경로)
                .excludePathPatterns(Constants.NO_NEED_AUTH_URLS); //제외하는 경로 등록
    }
}

