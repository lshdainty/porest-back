package com.lshdainty.porest.security.oauth2.provider;

import java.util.Collections;
import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {
    private final Map<String, Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        // 네이버는 response 객체 안에 실제 데이터가 있음
        this.attributes = getMapSafely(attributes, "response");
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
        return "naver";
    }

    @Override
    public String getEmail() {
        return getStringOrEmpty(attributes.get("email"));
    }

    @Override
    public String getName() {
        return getStringOrEmpty(attributes.get("name"));
    }

    @Override
    public String getPicture() {
        return getStringOrEmpty(attributes.get("profile_image"));
    }
}
