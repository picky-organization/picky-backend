package network.picky.web.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import network.picky.web.auth.dto.JwtAuthenticationToken;
import network.picky.web.auth.exception.TokenAuthenticationException;
import network.picky.web.auth.exception.TokenInvalidException;
import network.picky.web.auth.token.BearerTokenResolver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    private final String TOKEN = "the_token";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    @DisplayName("정상작동시")
    void doFilterInternal() throws ServletException, IOException {
        //given
        ProviderManager providerManager = Mockito.mock();
        Authentication authentication = new JwtAuthenticationToken(TOKEN);
        Mockito.when(providerManager.authenticate(Mockito.any())).thenReturn(authentication);
        BearerTokenResolver bearerTokenResolver = Mockito.mock();
        Mockito.when(bearerTokenResolver.resolve(Mockito.any())).thenReturn(this.TOKEN);

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(providerManager, bearerTokenResolver);

        HttpServletRequest request = Mockito.mock();
        HttpServletResponse response = Mockito.mock();
        FilterChain filterChain = Mockito.mock();

        //when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        JwtAuthenticationToken auth = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        //then
        assertEquals(auth.getToken(), TOKEN);
    }

    @Test
    @DisplayName("Token resolver 에서 예외를 던질떄")
    void doFilterInternalTokenResolverThrow() throws ServletException, IOException {
        //given
        ProviderManager providerManager = Mockito.mock();
        BearerTokenResolver bearerTokenResolver = Mockito.mock();
        Mockito.doThrow(TokenInvalidException.class).when(bearerTokenResolver).resolve(Mockito.any());

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(providerManager, bearerTokenResolver);

        HttpServletRequest request = Mockito.mock();
        HttpServletResponse response = Mockito.mock();
        FilterChain filterChain = Mockito.mock();

        //when.then
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //then
        Mockito.verify(providerManager, Mockito.never()).authenticate(Mockito.any());
    }

    @Test
    @DisplayName("ProviderManager 에서 예외를 던질 때")
    void doFilterInternalProviderManagerThrow() throws ServletException, IOException {
        //given
        ProviderManager providerManager = Mockito.mock();
        Mockito.when(providerManager.authenticate(Mockito.any())).thenThrow(TokenAuthenticationException.class);
        BearerTokenResolver bearerTokenResolver = Mockito.mock();
        Mockito.when(bearerTokenResolver.resolve(Mockito.any())).thenReturn(this.TOKEN);

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(providerManager, bearerTokenResolver);

        HttpServletRequest request = Mockito.mock();
        HttpServletResponse response = Mockito.mock();
        FilterChain filterChain = Mockito.mock();

        //when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //then
        assertEquals(SecurityContextHolder.getContext(), new SecurityContextImpl());
    }
}