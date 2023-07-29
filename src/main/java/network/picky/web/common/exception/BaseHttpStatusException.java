package network.picky.web.common.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class BaseHttpStatusException extends RuntimeException {
    private int status;
    private String statusName;

    public BaseHttpStatusException(String message) {
        super(message);
    }
}