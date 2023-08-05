package network.picky.web.auth.exception;

public class InvalidTokenException extends RuntimeException {

    private static final String MESSAGE = "토큰 형식이 잘못 되었습니다.";

    public InvalidTokenException() {
        super(MESSAGE);
    }
}
