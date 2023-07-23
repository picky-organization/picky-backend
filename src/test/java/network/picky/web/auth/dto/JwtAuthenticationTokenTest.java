package network.picky.web.auth.dto;

import network.picky.web.member.domain.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.core.Authentication;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationTokenTest {
    String token = "TOKEN";
    @Test
    void unauthenticated() {
        Authentication authToken = JwtAuthenticationToken.unauthenticated(token);

        assertNull(authToken.getPrincipal());
        assertEquals(authToken.getCredentials(), this.token);
        assertTrue(authToken.getAuthorities().isEmpty());
    }

    @Test
    void authenticated() {
        Long id = 1L;
        Set<RoleGrant> role = RoleGrant.createSingleGrant(Role.USER);
        Authentication authToken = JwtAuthenticationToken.authenticated(id, token, role);
        assertEquals(authToken.getPrincipal(), id);
        assertEquals(authToken.getCredentials(), token);
        assertTrue(authToken.getAuthorities().containsAll(role));
    }
}