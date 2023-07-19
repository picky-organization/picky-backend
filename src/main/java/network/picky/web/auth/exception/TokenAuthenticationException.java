package network.picky.web.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenAuthenticationException extends AuthenticationException {
    private static final String MESSAGE = "유효하지 않은 토큰 입니다.";
    public TokenAuthenticationException(String message){// 문자열을 매개변수로 받는 생성자
        super(message);// 조상인 Exception 클래스의 생성자를 호출한다.
    }
    public TokenAuthenticationException(){
        super(MESSAGE);
    }
}
