package network.picky.web.auth.exception;

public class TokenNotProvidedException extends RuntimeException{

    private static final String MESSAGE = "헤더에 토큰값이 들어오지 않았습니다.";

    public TokenNotProvidedException() {
        super(MESSAGE);
    }
}
