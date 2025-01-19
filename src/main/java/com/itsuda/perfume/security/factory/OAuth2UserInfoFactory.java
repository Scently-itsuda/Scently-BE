package com.itsuda.perfume.security.factory;

import com.itsuda.perfume.domain.type.EProvider;
import com.itsuda.perfume.security.KakaoOAuth2UserInfo;
import com.itsuda.perfume.security.NaverOAuth2UserInfo;
import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(EProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            case NAVER -> new NaverOAuth2UserInfo(attributes);
            default -> throw new IllegalArgumentException("Invalid Provider Type.");
        };
    }
}
