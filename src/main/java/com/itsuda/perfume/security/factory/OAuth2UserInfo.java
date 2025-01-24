package com.itsuda.perfume.security.factory;

import java.util.Map;
import lombok.Getter;

@Getter
public abstract class OAuth2UserInfo {
    protected final Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId();
    public abstract String getEmail();
    public abstract String getProfileImageUrl();
}
