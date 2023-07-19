package network.picky.web.auth.token;

import jakarta.servlet.http.HttpServletRequest;
import network.picky.web.auth.exception.TokenAuthenticationException;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

public class TokenResolver {
    private static final String AUTHORIZATION_PREFIX = "Bearer";
    private static final int AUTHORIZATION_PREFIX_INDEX = 7;

    public static String resolve(HttpServletRequest request){
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization != null && authorization.startsWith(AUTHORIZATION_PREFIX)) {
            String token = authorization.substring(AUTHORIZATION_PREFIX_INDEX);
            if (StringUtils.hasText(token)){
                return token;
            }
        }
        throw new TokenAuthenticationException();
    }
}
