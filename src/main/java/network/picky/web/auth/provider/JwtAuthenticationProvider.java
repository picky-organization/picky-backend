package network.picky.web.auth.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.auth.dto.AuthUser;
import network.picky.web.auth.dto.JwtAuthenticationToken;
import network.picky.web.auth.dto.RoleGrant;
import network.picky.web.auth.exception.TokenAuthenticationException;
import network.picky.web.auth.token.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken =(JwtAuthenticationToken) authentication;
        String token = jwtAuthenticationToken.getToken();
        if(jwtTokenProvider.validToken(token)){
            AuthUser authUser = jwtTokenProvider.getParseClaims(token);

            return JwtAuthenticationToken.authenticated(authUser.getId(), token, RoleGrant.createSingleGrant(authUser.getRole()));
        }
        throw new TokenAuthenticationException();
    }

    @Override
    public boolean supports(Class<?> authentication) {
            return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
