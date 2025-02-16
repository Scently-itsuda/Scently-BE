package com.itsuda.perfume.validator;

public class ValidationPatterns {
    // 닉네임 정규식 (한글, 영문, 숫자만 허용)
    public static final String NICKNAME_PATTERN = "^[a-zA-Z가-힣0-9]+$";
    // 이메일 정규식 (yyyy-MM-dd)
    public static final String DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";
}
