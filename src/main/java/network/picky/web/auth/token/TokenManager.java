package network.picky.web.auth.token;

import network.picky.web.auth.dto.AuthUser;

public interface TokenManager {

    String createAccessToken(AuthUser authUser);
    String createRefreshToken(AuthUser authUser);
    boolean validToken(String token);
    AuthUser getParseClaims(String token);
}
