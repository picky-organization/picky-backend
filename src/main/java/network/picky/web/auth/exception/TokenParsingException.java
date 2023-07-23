package network.picky.web.auth.exception;

public class TokenParsingException extends RuntimeException{
    private static final String MESSAGE = "파싱하지 못하는 토큰입니다.";
    public TokenParsingException(String message){// 문자열을 매개변수로 받는 생성자
        super(message);// 조상인 Exception 클래스의 생성자를 호출한다.
    }
    public TokenParsingException(){
        super(MESSAGE);
    }
}
