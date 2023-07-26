package network.picky.web.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.auth.domain.SavedToken;
import network.picky.web.auth.dto.AuthUser;
import network.picky.web.auth.repository.CookieAuthorizationRequestRepository;
import network.picky.web.auth.repository.SavedTokenRepository;
import network.picky.web.auth.token.JwtTokenProvider;
import network.picky.web.member.domain.Member;
import network.picky.web.member.enums.Role;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final SavedTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if (response.isCommitted()) {
            log.debug("Response has already been committed.");
            return;
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String authority = userPrincipal.getAuthorities().get(0).getAuthority();
        Role role = Role.valueOf(authority);
        AuthUser authUser = new AuthUser(userPrincipal.getId(), role);

        //JWT 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(authUser);
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(jwtTokenProvider.getRefreshTokenExpiredMilliseconds()/1000);
        response.addCookie(refreshCookie);

        Member member = new Member(authUser.getId());
        SavedToken savedTokenEntity = new SavedToken(member, refreshToken);
        refreshTokenRepository.save(savedTokenEntity);

        String accessToken = jwtTokenProvider.createAccessToken(authUser);
        String authorizationScheme = jwtTokenProvider.createAuthorizationScheme(accessToken);
        response.addHeader(HttpHeaders.AUTHORIZATION, authorizationScheme);

        response.setStatus(HttpServletResponse.SC_OK);
        log.info(refreshToken);
    }
//    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
//        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
//                .map(Cookie::getValue);
//
//        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
//            throw new RuntimeException("redirect URIs are not matched.");
//        }
//        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
//
//        UserPrincipal userPrincipal = (UserPrincipal) authentication;
//        String authority = userPrincipal.getAuthorities().get(0).getAuthority();
//        Role role = Role.valueOf(authority);
//        AuthUser authUser = new AuthUser(userPrincipal.getId(), role);
//        //JWT 생성
//        String accessToken = jwtTokenProvider.createAccessToken(authUser);
//        String refreshToken = jwtTokenProvider.createRefreshToken(authUser);
//        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
//        refreshCookie.setHttpOnly(true);
//        response.addCookie(refreshCookie);
//
//        return UriComponentsBuilder.fromUriString(targetUrl)
//                .queryParam("token", tokenInfo.getAccessToken())
//                .build().toUriString();
//    }
//
//    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
//        super.clearAuthenticationAttributes(request);
//        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
//    }
//
//    private boolean isAuthorizedRedirectUri(String uri) {
//        URI clientRedirectUri = URI.create(uri);
//        URI authorizedUri = URI.create(redirectUri);
//
//        if (authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
//                && authorizedUri.getPort() == clientRedirectUri.getPort()) {
//            return true;
//        }
//        return false;
//    }

}
