package network.picky.web.auth.provider;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import network.picky.web.auth.domain.AuthUser;
import network.picky.web.auth.jwt.token.JwtAuthenticationToken;
import network.picky.web.auth.domain.RoleGrant;
import network.picky.web.auth.exception.TokenAuthenticationException;
import network.picky.web.auth.exception.TokenInvalidException;
import network.picky.web.auth.exception.TokenParsingException;
import network.picky.web.auth.jwt.token.JwtAuthenticationProvider;
import network.picky.web.auth.jwt.token.JwtTokenProvider;
import network.picky.web.member.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtAuthenticationProviderTest {
    public JwtTokenProvider jwtTokenProvider;
    public String token;
    public JwtAuthenticationProvider jwtAuthenticationProvider;
    public String secretKey = "&3YZpPi--/#R}e?~35$gw8&TW'?KM2Kj_ql5RXY2Xq!M'j58bFgY$iO_|0Uoek2";
    public int accessTokenExpired = 1000 * 60 * 60 * 2;
    public int refreshTokenExpired = 1000 * 60 * 60 * 2;
    AuthUser authUser;

    @BeforeEach
    public void setUp() {
        this.jwtTokenProvider = new JwtTokenProvider(secretKey, accessTokenExpired, refreshTokenExpired);
        this.jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtTokenProvider);
        Long id = 1L;
        Role role = Role.USER;
        authUser = new AuthUser(id, role);
        this.token = jwtTokenProvider.createAccessToken(authUser);
    }

    @Test
    @DisplayName("authenticate 정상 작동 테스트")
    void authenticate() {
        //given
        Authentication authentication = new JwtAuthenticationToken(this.token);
        Authentication expect = JwtAuthenticationToken.authenticated(authUser.getId(), token, RoleGrant.createSingleGrant(authUser.getRole()));

        //when
        Authentication actual = jwtAuthenticationProvider.authenticate(authentication);

        //then
        assertEquals(expect, actual);

    }

    @Test
    @DisplayName("authenticate token이 유효하지 않을 경우")
    void authenticateNotJwtToken() {
        //given
        String strangeToken = "ThisIsToken";
        Authentication authentication = new JwtAuthenticationToken(strangeToken);

        //when.then
        assertThrows(TokenAuthenticationException.class, () -> jwtAuthenticationProvider.authenticate(authentication));
    }

    @Test
    @DisplayName("authenticate token 값을 가지고 있지 않을 경우")
    void authenticateNoTken() {
        //given
        Authentication authentication = JwtAuthenticationToken.unauthenticated(null);

        //when.then
        TokenAuthenticationException a = assertThrows(TokenAuthenticationException.class, () -> jwtAuthenticationProvider.authenticate(authentication));
        assertEquals(a.getCause().getClass(), TokenInvalidException.class);
    }

    @Test
    @DisplayName("authenticate AuthUser.role 값을 가지고 있지 않을 경우")
    void authenticateNoRole() {
        //given
        Date now = new Date();
        Date expiration = new Date(now.getTime() + this.accessTokenExpired);
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String token = Jwts.builder()
                .setIssuedAt(now)
                .setSubject(authUser.getId().toString())
                .setExpiration(expiration)
                .signWith(key)
                .compact();
        Authentication authentication = JwtAuthenticationToken.unauthenticated(token);

        //when.then
        TokenAuthenticationException a = assertThrows(TokenAuthenticationException.class, () -> jwtAuthenticationProvider.authenticate(authentication));
        assertEquals(a.getCause().getClass(), TokenParsingException.class);
    }

    @Test
    @DisplayName("authenticate AuthUser.id 값을 가지고 있지 않을 경우")
    void authenticateNoId() {
        //given
        Role role = authUser.getRole();
        Map<String, Object> claim = new HashMap<>();
        claim.put("role", role);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + this.accessTokenExpired);

        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        Key key = Keys.hmacShaKeyFor(keyBytes);

        String token = Jwts.builder()
                .setIssuedAt(now)
                .addClaims(claim)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
        Authentication authentication = JwtAuthenticationToken.unauthenticated(token);

        //when.then
        TokenAuthenticationException a = assertThrows(TokenAuthenticationException.class, () -> jwtAuthenticationProvider.authenticate(authentication));
        assertEquals(a.getCause().getClass(), TokenParsingException.class);
    }

    @Test
    @DisplayName("authenticate token이 만료될 경우")
    void authenticateExpiredToken() {
        //given
        int zeroExpired = 0;
        this.jwtTokenProvider = new JwtTokenProvider(secretKey, zeroExpired, refreshTokenExpired);
        this.jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtTokenProvider);
        this.token = jwtTokenProvider.createAccessToken(authUser);
        Authentication authentication = JwtAuthenticationToken.unauthenticated(token);

        //when.then
        TokenAuthenticationException a = assertThrows(TokenAuthenticationException.class, () -> jwtAuthenticationProvider.authenticate(authentication));
        assertEquals(a.getCause().getClass(), TokenInvalidException.class);
    }
}