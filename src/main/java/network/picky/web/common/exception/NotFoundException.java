package network.picky.web.common.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
public class NotFoundException extends BaseHttpStatusException {

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public int getStatus() {
        return HttpStatus.NOT_FOUND.value();
    }

    @Override
    public String getStatusName() {
        return HttpStatus.NOT_FOUND.getReasonPhrase();
    }
}
