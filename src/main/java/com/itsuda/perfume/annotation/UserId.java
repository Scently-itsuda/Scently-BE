package com.itsuda.perfume.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@Retention : 어느 시점까지 어노테이션의 메모리를 가져갈 지 설정
//@Target : 어노테이션이 사용될 위치를 지정한다. EX. @Target(METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME) //런타임까지 어노테이션 정보를 유지
@Target(ElementType.PARAMETER) //파라미터에만 적용
public @interface UserId {
    boolean required() default true;
}
