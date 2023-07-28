package network.picky.web.common.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
public class BadRequestException extends BaseHttpStatusException {

    public BadRequestException(String message) {
        super(message);
    }

    @Override
    public int getStatus() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getStatusName() {
        return HttpStatus.BAD_REQUEST.getReasonPhrase();
    }
}
