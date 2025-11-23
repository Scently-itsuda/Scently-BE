package com.itsuda.perfume.domain.type;

import lombok.Getter;

@Getter
public enum ReportType {
    SPAM_AD("스팸, 광고"),
    INAPPROPRIATE_LANGUAGE("욕설, 인신공격 등의 부적절한 발언"),
    SEXUAL_CONTENT("음란성, 선정성 글"),
    SPAMMING("반복적인 글 게재"),
    PERSONAL_INFORMATION_LEAK("개인정보 등 민감정보 노출"),
    OTHER("기타");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }
}
