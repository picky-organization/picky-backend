package network.picky.web.auth.exception;

import org.springframework.security.core.AuthenticationException;


public class DuplicatedAuthenticationException extends AuthenticationException {

    public DuplicatedAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
