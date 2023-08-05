package network.picky.web.auth.domain;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import network.picky.web.member.enums.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.Set;

@EqualsAndHashCode
@RequiredArgsConstructor
public class RoleGrant implements GrantedAuthority {
    private final Role role;

    public static Set<RoleGrant> createSingleGrant(Role role) {
        return Collections.singleton(new RoleGrant(role));
    }

    @Override
    public String getAuthority() {
        return role.getKey();
    }
}
