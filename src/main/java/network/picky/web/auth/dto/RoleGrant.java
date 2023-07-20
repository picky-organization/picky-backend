package network.picky.web.auth.dto;

import lombok.RequiredArgsConstructor;
import network.picky.web.member.domain.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public class RoleGrant implements GrantedAuthority {
    private final Role role;

    @Override
    public String getAuthority() {
        return role.getKey();
    }

    public static Set<RoleGrant> createSingleGrant(Role role){
        return Collections.singleton(new RoleGrant(role));
    }
}
