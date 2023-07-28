package network.picky.web.auth.exception;

public class TokenInvalidException extends RuntimeException {
    private static final String MESSAGE = "유효하지 않은 토큰입니다.";

    public TokenInvalidException(String message) {// 문자열을 매개변수로 받는 생성자
        super(message);// 조상인 Exception 클래스의 생성자를 호출한다.
    }

    public TokenInvalidException() {
        super(MESSAGE);
    }
}
