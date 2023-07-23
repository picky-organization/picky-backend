package network.picky.web.auth.token;

import network.picky.web.auth.dto.AuthUser;
import network.picky.web.member.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setup(){
        String secretKey ="&3YZpPi--/#R}e?~35$gw8&TW'?KM2Kj_ql5RXY2Xq!M'j58bFgY$iO_|0Uoek2";
        int accessTokenExpired = 1000 * 60 * 60 * 2;
        int refreshTokenExpired = 1000 * 60 * 60 * 2;

        this.jwtTokenProvider = new JwtTokenProvider(secretKey, accessTokenExpired, refreshTokenExpired);
    }

    @Test
    @DisplayName("accessToken 만들고 검증")
    void createAccessToken() {
        //given
        Long id = 1L;
        AuthUser authUser = new AuthUser(id, Role.USER);

        //when
        String token = this.jwtTokenProvider.createAccessToken(authUser);

        //then
        assertTrue(this.jwtTokenProvider.validToken(token));
    }

    @Test
    @DisplayName("accessToken claim 확인")
    void createAccessTokenCheckClaim() {
        //given
        Long id = 1L;
        AuthUser authUser = new AuthUser(id, Role.USER);

        //when
        String token = this.jwtTokenProvider.createAccessToken(authUser);
        AuthUser authUserParse=this.jwtTokenProvider.getParseClaims(token);

        //then
        assertEquals(authUserParse, authUserParse);
    }


    @Test
    @DisplayName("refresh_token 화인")
    void createRefreshToken() {
        //given
        Long id = 1L;
        AuthUser authUser = new AuthUser(id, Role.USER);

        //when
        String token = this.jwtTokenProvider.createRefreshToken(authUser);

        //then
        assertTrue(this.jwtTokenProvider.validToken(token));
    }

    @Test
    @DisplayName("refresh_token claim 확인")
    void createRefreshTokenCheckClaim() {
        //given
        Long id = 1L;
        AuthUser authUser = new AuthUser(id, Role.USER);

        //when
        String token = this.jwtTokenProvider.createRefreshToken(authUser);
        AuthUser authUserParse=this.jwtTokenProvider.getParseClaims(token);

        //then
        assertEquals(authUserParse, authUserParse);
    }

    @Test
    void createAuthorizationScheme() {
        //given
        String token = "this_is_token";

        //when
        String tokenHeader = this.jwtTokenProvider.createAuthorizationScheme(token);

        //then
        assertEquals(tokenHeader.split(" ")[0], JwtTokenProvider.AUTHORIZATION_PREFIX);
        assertEquals(tokenHeader.split(" ")[1], token);

    }
}