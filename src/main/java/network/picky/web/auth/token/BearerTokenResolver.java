package network.picky.web.auth.token;

import jakarta.servlet.http.HttpServletRequest;
import network.picky.web.auth.exception.TokenInvalidException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BearerTokenResolver{
    private static final String AUTHORIZATION_PREFIX = "Bearer";
    private static final int AUTHORIZATION_PREFIX_INDEX = 7;

    public String resolve(HttpServletRequest request){
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null && authorization.startsWith(AUTHORIZATION_PREFIX)) {
            if (authorization.charAt(AUTHORIZATION_PREFIX_INDEX) == ' '){
                String token = authorization.substring(AUTHORIZATION_PREFIX_INDEX);
                if (StringUtils.hasText(token)){
                    return token;
                }
            }
        }
        throw new TokenInvalidException();
    }
}
