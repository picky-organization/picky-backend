package network.picky.web.auth.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import network.picky.web.auth.dto.JwtAuthenticationToken;
import network.picky.web.auth.exception.TokenInvalidException;
import network.picky.web.auth.token.TokenResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter  {
	private final ProviderManager providerManager;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String token = null;
		try {
			token = TokenResolver.resolve(request);
		}catch (TokenInvalidException invalid){
			filterChain.doFilter(request, response);
			return;
		}

		Authentication jwtAuthenticationToken = new JwtAuthenticationToken(token);

		try{
			Authentication authenticationResult = providerManager.authenticate(jwtAuthenticationToken);

			SecurityContext context = SecurityContextHolder.createEmptyContext();
			context.setAuthentication(authenticationResult);
			SecurityContextHolder.setContext(context);

			filterChain.doFilter(request, response);
		}catch (AuthenticationException failed){
			SecurityContextHolder.clearContext();
		}
	}
}