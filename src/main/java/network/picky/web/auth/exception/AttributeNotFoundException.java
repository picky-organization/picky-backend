package network.picky.web.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class AttributeNotFoundException extends AuthenticationException {

    public AttributeNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
