package network.picky.web.tech.exception;

import lombok.Getter;
import network.picky.web.common.exception.ConflictException;

@Getter
public class TechExistsException extends ConflictException {
    private static final String message = "이미 기술이 존재합니다.";

    public TechExistsException() {
        super(message);
    }
}
