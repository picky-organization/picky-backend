package network.picky.web.auth.dto;

import lombok.*;
import network.picky.web.member.domain.Role;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class AuthUser{
    private final Long id;
    private final Role role;

}
