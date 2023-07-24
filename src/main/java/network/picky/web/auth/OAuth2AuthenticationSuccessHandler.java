package network.picky.web.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.auth.dto.AuthUser;
import network.picky.web.auth.repository.CookieAuthorizationRequestRepository;
import network.picky.web.auth.token.JwtTokenProvider;
import network.picky.web.member.domain.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${oauth.authorizedRedirectUri}")
    private String redirectUri;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed.");
            return;
        }
//        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

        UserPrincipal userPrincipal = (UserPrincipal) authentication;
        String authority = userPrincipal.getAuthorities().get(0).getAuthority();
        Role role = Role.valueOf(authority);
        AuthUser authUser = new AuthUser(userPrincipal.getId(), role);
        //JWT 생성
        String accessToken = jwtTokenProvider.createAccessToken(authUser);
        String refreshToken = jwtTokenProvider.createRefreshToken(authUser);
        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setHttpOnly(true);
        response.addCookie(refreshCookie);
        String authorizationScheme = jwtTokenProvider.createAuthorizationScheme(accessToken);
        response.addHeader(HttpHeaders.AUTHORIZATION, authorizationScheme);
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
