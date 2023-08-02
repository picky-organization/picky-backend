package network.picky.web.auth.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import network.picky.web.member.enums.Role;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class AuthUser {
    private final Long id;
    private final Role role;
}
