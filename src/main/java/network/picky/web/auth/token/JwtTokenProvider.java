package network.picky.web.auth.token;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import network.picky.web.auth.dto.AuthUser;
import network.picky.web.member.domain.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider implements TokenProvider {
	private final Key key;
	private final Long accessTokenExpiredMilliseconds;
	private final Long refreshTokenExpiredMilliseconds;

	public JwtTokenProvider(@Value("${security.jwt.token.secret-key}") String secretKey,
							@Value("${security.jwt.token.expired.access}") Long accessTokenExpiredMilliseconds,
							@Value("${security.jwt.token.expired.refresh}") Long refreshTokenExpiredMilliseconds) {
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.accessTokenExpiredMilliseconds = accessTokenExpiredMilliseconds;
		this.refreshTokenExpiredMilliseconds = refreshTokenExpiredMilliseconds;
	}

	public String createAccessToken(AuthUser authUser) {
		Role role = authUser.getRole();
		Map<String, Object> claim = new HashMap<>();
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
		Date now = new Date();
		Date expiration = new Date(now.getTime() + this.accessTokenExpiredMilliseconds);

		return Jwts.builder()
				.setSubject(authUser.getId().toString())
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
		Claims body = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		Long id = Long.parseLong(body.getSubject());
		Role role = (Role) body.get("role");
		AuthUser authUser = new AuthUser(id, role);

		return authUser;
	}


	public String createAuthorizationScheme(String token){
		final String AUTHORIZATION_PREFIX = "Bearer";

		return AUTHORIZATION_PREFIX + " " + token;
	}
}

