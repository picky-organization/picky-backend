package network.picky.web.auth.dto;

import network.picky.web.auth.enums.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes, String accessToken) {
        return switch (authProvider) {
            case GOOGLE -> new GoogleOAuth2User(attributes);
            case GITHUB -> new GithubOAuth2User(attributes, accessToken);
        };
    }
}
