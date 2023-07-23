package network.picky.web.auth.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import network.picky.web.member.domain.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class AuthUser{
    private final Long id;
    private final Role role;
}
