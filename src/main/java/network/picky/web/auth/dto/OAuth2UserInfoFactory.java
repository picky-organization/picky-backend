package network.picky.web.auth.dto;

import network.picky.web.auth.GoogleOAuth2User;
import network.picky.web.auth.dto.OAuth2UserInfo;
import network.picky.web.auth.enums.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        switch (authProvider) {
            case GOOGLE: return new GoogleOAuth2User(attributes);

            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
