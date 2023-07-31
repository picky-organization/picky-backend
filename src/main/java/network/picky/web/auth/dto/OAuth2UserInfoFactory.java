package network.picky.web.auth.dto;

import network.picky.web.auth.GithubOAuth2User;
import network.picky.web.auth.GoogleOAuth2User;
import network.picky.web.auth.enums.AuthProvider;
import org.json.simple.parser.ParseException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes, String accessToken) {
        switch (authProvider) {
            case GOOGLE:
                return new GoogleOAuth2User(attributes);
            case GITHUB:
                return new GithubOAuth2User(attributes, accessToken);
            default:
                throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
