package network.picky.web.auth.dto;

import java.util.Map;

public class GoogleOAuth2User extends OAuth2UserInfo {

    public GoogleOAuth2User(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getPicture() {
        return (String) attributes.get("picture");
    }
}
