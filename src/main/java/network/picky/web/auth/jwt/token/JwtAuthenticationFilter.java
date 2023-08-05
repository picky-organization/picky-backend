package network.picky.web.auth.jwt.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import network.picky.web.auth.exception.TokenInvalidException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final ProviderManager providerManager;
    private final BearerTokenResolver bearerTokenResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token;
        try {
            token = bearerTokenResolver.resolve(request);
        } catch (TokenInvalidException invalid) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication jwtAuthenticationToken = new JwtAuthenticationToken(token);

        try {
            Authentication authenticationResult = providerManager.authenticate(jwtAuthenticationToken);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationResult);
            SecurityContextHolder.setContext(context);
        } catch (AuthenticationException failed) {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}