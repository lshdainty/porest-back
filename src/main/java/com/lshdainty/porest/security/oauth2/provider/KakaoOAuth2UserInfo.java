package com.lshdainty.porest.security.oauth2.provider;

import java.util.Collections;
import java.util.Map;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes != null ? attributes : Collections.emptyMap();
        this.kakaoAccount = getMapSafely(this.attributes, "kakao_account");
        this.profile = getMapSafely(kakaoAccount, "profile");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapSafely(Map<String, Object> map, String key) {
        if (map == null) {
            return Collections.emptyMap();
        }
        Object value = map.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return Collections.emptyMap();
    }

    private String getStringOrEmpty(Object value) {
        if (value instanceof String) {
            return (String) value;
        }
        return "";
    }

    @Override
    public String getProviderId() {
        Object id = attributes.get("id");
        return id != null ? String.valueOf(id) : "";
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return getStringOrEmpty(kakaoAccount.get("email"));
    }

    @Override
    public String getName() {
        return getStringOrEmpty(profile.get("nickname"));
    }

    @Override
    public String getPicture() {
        return getStringOrEmpty(profile.get("profile_image_url"));
    }
}
