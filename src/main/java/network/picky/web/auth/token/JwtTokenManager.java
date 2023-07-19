package network.picky.web.auth.token;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import network.picky.web.auth.dto.AuthUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenManager implements TokenManager {
	private final Key key;
	private final Long accessTokenExpiredMilliseconds;
	private final Long refreshTokenExpiredMilliseconds;

	public JwtTokenManager(@Value("${security.jwt.token.secret-key}") String secretKey,
						   @Value("${security.jwt.token.expired.access}") Long accessTokenExpiredMilliseconds,
						   @Value("${security.jwt.token.expired.refresh}")Long refreshTokenExpiredMilliseconds) {
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.accessTokenExpiredMilliseconds = accessTokenExpiredMilliseconds;
		this.refreshTokenExpiredMilliseconds = refreshTokenExpiredMilliseconds;
	}

	public String createAccessToken(AuthUser authUser) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + this.accessTokenExpiredMilliseconds);
		return Jwts.builder()
				.setId(authUser.getId().toString())
				.setIssuedAt(now)
				.setExpiration(expiration).signWith(key).compact();
	}

	@Override
	public String createRefreshToken(AuthUser authUser) {
		Date now = new Date();
		Date expiration = new Date(now.getTime() + this.accessTokenExpiredMilliseconds);
		return Jwts.builder()
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
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	}

	public AuthUser getParseClaims(String token) {
		Long id = Long.parseLong(Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getId());
		AuthUser authUser = new AuthUser(id);
		return authUser;
	}

	public String createAuthorizationScheme(String token){
		final String AUTHORIZATION_PREFIX = "Bearer";
		return AUTHORIZATION_PREFIX + " " + token;
	}
}

