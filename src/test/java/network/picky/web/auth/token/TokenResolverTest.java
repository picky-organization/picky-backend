package network.picky.web.auth.token;

import jakarta.servlet.http.HttpServletRequest;
import network.picky.web.auth.exception.TokenInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TokenResolverTest {
    @Mock
    HttpServletRequest request;
    BearerTokenResolver bearerTokenResolver;

    @BeforeEach
    public void setUp() {
        bearerTokenResolver = new BearerTokenResolver();
    }

    @Test
    @DisplayName("Authorization Header가 없는 경우")
    public void testResolveNotExistsAuthorizationHeader() {
        //given
        Mockito.when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("");

        //then.when
        assertThrows(TokenInvalidException.class, () ->
                bearerTokenResolver.resolve(request));
    }

    @Test
    @DisplayName("Authroization Header가 Bearer가 아닌 경우")
    public void testResolveAuthorizationHeaderNotBearer() {
        //given
        String token = "secret";
        String tokenPrefix = "Token";
        String header = tokenPrefix + " " + token;
        Mockito.when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(header);

        //then.when
        assertThrows(TokenInvalidException.class, () ->
                bearerTokenResolver.resolve(request));
    }

    @Test
    @DisplayName("헤더사이에 스페이스가 없는 경우")
    public void testResolveHeaderNoSpace() {
        //given
        String token = "secret";
        String tokenHeader = "Bearer";
        String header = tokenHeader + token;
        Mockito.when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(header);

        //then
        assertThrows(TokenInvalidException.class, () ->
                bearerTokenResolver.resolve(request));
    }

}