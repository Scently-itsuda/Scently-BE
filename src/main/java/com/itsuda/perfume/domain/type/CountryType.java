package com.itsuda.perfume.domain.type;

import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import lombok.Getter;

@Getter
public enum CountryType {
    FRANCE("프랑스"),
    ITALY("이탈리아"),
    USA("미국"),
    UK("영국"),
    KOREA("한국"),
    SWEDEN("스웨덴");
    
    private final String description;
    
    CountryType(String description) {
        this.description = description;
    }

    public static CountryType of(String description) {
        for (CountryType countryType : CountryType.values()) {
            if (countryType.getDescription().equals(description)) {
                return countryType;
            }
        }
        throw new RestApiException(ErrorCode.INVALID_COUNTRY_TYPE);
    }
} 