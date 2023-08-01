package network.picky.web.auth.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.auth.dto.AuthUser;
import network.picky.web.auth.exception.TokenParsingException;
import network.picky.web.member.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Component
public class JwtTokenProvider implements TokenProvider {
    public static final String AUTHORIZATION_PREFIX = "Bearer";

    private final Key key;
    private final int accessTokenExpiredMilliseconds;
    private final int refreshTokenExpiredMilliseconds;

    public JwtTokenProvider(@Value("${security.jwt.token.secret-key}") String secretKey,
                            @Value("${security.jwt.token.expired.access}") int accessTokenExpiredMilliseconds,
                            @Value("${security.jwt.token.expired.refresh}") int refreshTokenExpiredMilliseconds) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiredMilliseconds = accessTokenExpiredMilliseconds;
        this.refreshTokenExpiredMilliseconds = refreshTokenExpiredMilliseconds;
    }


    @Override
    public String createAccessToken(AuthUser authUser) {
        Map<String, Object> claim = new HashMap<>();
        Role role = authUser.getRole();
        claim.put("role", role);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + this.accessTokenExpiredMilliseconds);

        return Jwts.builder()
                .setSubject(authUser.getId().toString())
                .addClaims(claim)
                .setIssuedAt(now)
                .setExpiration(expiration).signWith(key).compact();
    }

    @Override
    public String createRefreshToken(AuthUser authUser) {
        Role role = authUser.getRole();
        Map<String, Object> claim = new HashMap<>();
        claim.put("role", role);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + this.refreshTokenExpiredMilliseconds);

        return Jwts.builder()
                .setSubject(authUser.getId().toString())
                .addClaims(claim)
                .setIssuedAt(now)
                .setExpiration(expiration).signWith(key).compact();
    }

    public boolean validToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (SecurityException | MalformedJwtException e) {
            log.debug("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.debug("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.debug("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.debug("JWT claims string is empty.", e);
        }
        return false;
    }

    public AuthUser getParseClaims(String token) {
        Claims body = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        try {
            Long id = Long.parseLong(body.getSubject());
            Role role = Role.valueOf(String.valueOf(body.get("role")));
            return new AuthUser(id, role);
        } catch (Exception ex) {
            log.debug("JwtTokenProvider parsing faild");
            throw new TokenParsingException();
        }
    }

    public String createAuthorizationScheme(String token) {

        return AUTHORIZATION_PREFIX + " " + token;
    }
}

