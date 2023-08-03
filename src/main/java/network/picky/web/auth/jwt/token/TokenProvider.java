package network.picky.web.auth.jwt.token;

import network.picky.web.auth.dto.AuthUser;

public interface TokenProvider {

    String createAccessToken(AuthUser authUser);

    String createRefreshToken(AuthUser authUser);

    boolean validToken(String token);

    AuthUser getParseClaims(String token);
}
