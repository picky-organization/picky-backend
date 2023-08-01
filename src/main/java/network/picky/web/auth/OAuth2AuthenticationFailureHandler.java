package network.picky.web.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import network.picky.web.auth.repository.CookieAuthorizationRequestRepository;
import network.picky.web.common.error.ErrorResponse;
import network.picky.web.common.exception.BaseHttpStatusException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        BaseHttpStatusException cause = (BaseHttpStatusException)authenticationException.getCause();
        String message = authenticationException.getMessage();
        String currentPath = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();
        ObjectWriter objectWriter = new ObjectMapper().registerModule(new JavaTimeModule()).writer().withDefaultPrettyPrinter();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .path(currentPath)
                .status(cause.getStatus())
                .statusName(cause.getStatusName())
                .message(message)
                .build();
        String json = objectWriter.writeValueAsString(errorResponse);

        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        response.getWriter().write(json);
    }


}
