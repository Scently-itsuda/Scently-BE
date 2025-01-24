package com.itsuda.perfume.intercepter;

import com.itsuda.perfume.annotation.UserId;
import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Slf4j
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {

    // 1. 이 리졸버를 적용할 파라미터 판단
    //    supportsParameter() : @UserId 애노테이션이 있으면서 Long 타입이면 해당 ArgumentResolver가 사용된다.
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(Long.class) //파라미터가 Long 타입이고
                && parameter.hasParameterAnnotation(UserId.class); //UserId 어노테이션일 때만 resolver 적용
    }

    // 2. 실제 파라미터에 주입할 값을 처리
    //    resolveArgument() : 컨트롤러 호출 직전에 호출 되어서 필요한 파라미터 정보를 생성해준다. 여기서는 세션
    //    에 있는 로그인 회원 정보인 member 객체를 찾아서 반환해준다. 이후 스프링MVC는 컨트롤러의 메서드를 호출
    //    하면서 여기에서 반환된 member 객체를 파라미터에 전달해준다
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        // 인터셉터가 저장해둔 "USER_ID" 속성을 Request에서 가져옴 → Long 타입 변환
        final Object userIdObj = webRequest.getAttribute("USER_ID", WebRequest.SCOPE_REQUEST);

        //없으면 예외처리
        if (userIdObj == null) {
            log.info("User_ID 없음");
            throw new RestApiException(ErrorCode.ACCESS_DENIED_ERROR);
        }
        //Long 타입으로 변환해 반환
        return Long.valueOf(userIdObj.toString());
    }
}