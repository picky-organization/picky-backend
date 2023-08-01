package network.picky.web.auth.exception;

public class TokenInvalidException extends RuntimeException {
    private static final String MESSAGE = "유효하지 않은 토큰입니다.";

    public TokenInvalidException() {
        super(MESSAGE);
    }
}
