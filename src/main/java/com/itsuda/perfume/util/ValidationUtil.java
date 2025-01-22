package com.itsuda.perfume.util;

import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;

public class ValidationUtil {
    private static final String DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";

    public static boolean isValidDateFormat(String date) {
        return date == null || date.matches(DATE_PATTERN);
    }
} 