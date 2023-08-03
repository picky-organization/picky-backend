package network.picky.web.auth.dto;

import lombok.Getter;
import network.picky.web.member.enums.Role;

@Getter
public class AuthUser {
    private Long id;
    private Role role;

    public AuthUser(Long id, Role role) {
        this.id = id;
        this.role = role;
    }
}
