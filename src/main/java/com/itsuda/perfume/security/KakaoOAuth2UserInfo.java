package com.itsuda.perfume.security;

import com.itsuda.perfume.domain.type.GenderType;
import com.itsuda.perfume.security.factory.OAuth2UserInfo;
import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {
    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public GenderType getGender() {
        return null;
    }

    @Override
    public String getEmail() {
        return "";
    }
}
