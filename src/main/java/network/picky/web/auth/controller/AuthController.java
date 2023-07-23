package network.picky.web.auth.controller;


import lombok.RequiredArgsConstructor;
import network.picky.web.auth.domain.RefreshToken;
import network.picky.web.auth.dto.AuthUser;
import network.picky.web.auth.repository.RefreshTokenRepository;
import network.picky.web.auth.token.JwtTokenProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("auth")
@RestController
public class AuthController {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenManager;

    @GetMapping("refresh")
    public ResponseEntity<Void> refresh(@CookieValue(value="refresh_token") String refreshToken){
        if(StringUtils.hasText(refreshToken) && jwtTokenManager.validToken(refreshToken)) {
            if(refreshTokenRepository.findByRefreshToken(refreshToken)){
                AuthUser authUser = jwtTokenManager.getParseClaims(refreshToken);
                String accessToken = jwtTokenManager.createAccessToken(authUser);
                String tokenHeader = jwtTokenManager.createAuthorizationScheme(accessToken);

                return ResponseEntity.created(null).header(HttpHeaders.AUTHORIZATION, tokenHeader).build();
            }
        }
        return ResponseEntity.badRequest().build();
    }
}