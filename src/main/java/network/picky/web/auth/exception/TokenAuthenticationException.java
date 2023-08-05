package network.picky.web.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenAuthenticationException extends AuthenticationException {
    private static final String MESSAGE = "유효하지 않은 토큰 입니다.";

    public TokenAuthenticationException() {
        super(MESSAGE);
    }
}
