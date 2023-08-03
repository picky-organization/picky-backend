package network.picky.web.auth.dto;

import lombok.Getter;
import network.picky.web.member.enums.Role;

@Getter
public class AuthUser {
    private final Long id;
    private final Role role;

    public AuthUser(Long id, Role role) {
        this.id = id;
        this.role = role;
    }
}
