package network.picky.web.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("ROLE_ADMIN", "관리자"),
    MANAGER("ROLE_MANAGER", "매니저"),
    USER("ROLE_USER", "사용자");

    private final String key;
    private final String title;

    public static Role of(String code){
        return Arrays.stream(Role.values())
                .filter(r -> r.getKey().equals(code))
                .findAny()
                .orElse(USER);
    }

}

