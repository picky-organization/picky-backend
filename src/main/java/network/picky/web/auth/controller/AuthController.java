package network.picky.web.auth.controller;


import lombok.RequiredArgsConstructor;
import network.picky.web.auth.dto.AuthUser;
import network.picky.web.auth.repository.SavedTokenRepository;
import network.picky.web.auth.token.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("auth")
@RestController
public class AuthController {
    private final SavedTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenManager;

    @GetMapping("refresh")
    public ResponseEntity<Void> refresh(@CookieValue(value = "refresh_token") String refreshToken) {
        if (StringUtils.hasText(refreshToken) && jwtTokenManager.validToken(refreshToken)) {
            try {
                refreshTokenRepository.findByRefreshToken(refreshToken);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }
            AuthUser authUser = jwtTokenManager.getParseClaims(refreshToken);
            String accessToken = jwtTokenManager.createAccessToken(authUser);
            String tokenHeader = jwtTokenManager.createAuthorizationScheme(accessToken);
            return ResponseEntity.created(null).header(HttpHeaders.AUTHORIZATION, tokenHeader).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("oauth")
    public ResponseEntity oauth() {
        Map<String, String> loginPathMap = new HashMap<>();
        URI uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/oauth2/authorization/google").build().toUri();
        loginPathMap.put("google", uri.toString());
        return ResponseEntity.ok().body(loginPathMap);
    }
}