package com.itsuda.perfume.validator;

import java.util.List;

public class ValidationConstants {
    public static final List<String> FORBIDDEN_WORDS = List.of(
            // 한글 금칙어
            "시발", "씨발",
            "병신",
            "좆", "존나", "좆나",
            "개새끼", "개새꺄",
            "미친", "미친새끼",
            "지랄",

            // 영어 금칙어
            "fuck"
    );
} 