package network.picky.web.tech.exception;

import lombok.Getter;
import network.picky.web.common.exception.NotFoundException;

@Getter
public class TechNotFoundException extends NotFoundException {
    private static final String message = "기술이 존재하지 않습니다.";

    public TechNotFoundException() {
        super(message);
    }
}
