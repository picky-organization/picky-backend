package network.picky.web.common.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
public class UnprocessableEntityException extends BaseHttpStatusException {

    public UnprocessableEntityException(String message) {
        super(message);
    }

    @Override
    public int getStatus() {
        return HttpStatus.UNPROCESSABLE_ENTITY.value();
    }

    @Override
    public String getStatusName() {
        return HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase();
    }
}
