package com.itsuda.perfume.domain.type;

import com.itsuda.perfume.exception.ErrorCode;
import com.itsuda.perfume.exception.RestApiException;
import lombok.Getter;

@Getter
public enum BrandType {
    CHANEL("샤넬"),
    DIOR("디올"),
    DIPTYQUE("딥디크"),
    GUCCI("구찌"),
    BYREDO("바이레도"),
    CREED("크리드"),
    JO_MALONE("조 말론"),
    TOM_FORD("톰 포드"),
    HERMES("에르메스"),
    LE_LABO("르 라보"),
    ACQUA_DI_PARMA("아쿠아 디 파르마");
    
    private final String description;
    
    BrandType(String description) {
        this.description = description;
    }

    public static BrandType of(String description) {
        for (BrandType brandType : BrandType.values()) {
            if (brandType.getDescription().equals(description)) {
                return brandType;
            }
        }
        throw new RestApiException(ErrorCode.INVALID_BRAND_TYPE);
    }
} 