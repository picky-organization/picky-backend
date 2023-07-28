package network.picky.web.common.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
public class ConflictException extends BaseHttpStatusException {

    public ConflictException(String message) {
        super(message);
    }

    @Override
    public int getStatus() {
        return HttpStatus.CONFLICT.value();
    }

    @Override
    public String getStatusName() {
        return HttpStatus.CONFLICT.getReasonPhrase();
    }
}
