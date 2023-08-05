package network.picky.web.auth.exception;

public class TokenParsingException extends RuntimeException {
    private static final String MESSAGE = "파싱하지 못하는 토큰입니다.";

    public TokenParsingException() {
        super(MESSAGE);
    }
}
