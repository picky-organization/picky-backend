package network.picky.web.auth;

import network.picky.web.auth.dto.OAuth2UserInfo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class GithubOAuth2User extends OAuth2UserInfo {

    private static final String apiUrl = "https://api.github.com/user/emails";
    private final String accessToken;

    public GithubOAuth2User(Map<String, Object> attributes, String accessToken){
        super(new HashMap<>(attributes));
        this.accessToken = accessToken;
        try {
            super.attributes.replace("email", getUserEmails());
        }catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String getEmail(){
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getPicture() {
        return (String) attributes.get("avatar_url");
    }

    public String getUserEmails() throws ParseException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github+json");
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);

        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(response.getBody());
        JSONObject object = (JSONObject) array.get(1);

        return (String)object.get("email");
    }

}
