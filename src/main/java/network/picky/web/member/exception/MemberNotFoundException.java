package network.picky.web.member.exception;

import lombok.Getter;
import network.picky.web.common.exception.NotFoundException;

@Getter
public class MemberNotFoundException extends NotFoundException {
    private static final String message = "멤버가 존재하지 않습니다.";
    public MemberNotFoundException() {
        super(message);
    }
}
