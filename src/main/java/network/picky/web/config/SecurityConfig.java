package network.picky.web.config;

import lombok.RequiredArgsConstructor;
import network.picky.web.auth.OAuth2AuthenticationFailureHandler;
import network.picky.web.auth.OAuth2AuthenticationSuccessHandler;
import network.picky.web.auth.filter.JwtAuthenticationFilter;
import network.picky.web.auth.repository.CookieAuthorizationRequestRepository;
import network.picky.web.auth.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.Provider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //httpBasic, csrf, formLogin, rememberMe, logout, session disable
        http
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .csrf(csrf -> csrf.disable())
                .rememberMe(rememberMe -> rememberMe.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //요청에 대한 권한 설정
        http
                .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/auth/refresh").permitAll()
                .anyRequest().authenticated());

        //oauth2Login
        http
                .oauth2Login(oauth-> oauth
                        .authorizationEndpoint(endpoint->endpoint
                                .baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository))
                        .redirectionEndpoint(endpoint->endpoint
                                .baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint(info->info
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler));
//                        .failureHandler(oAuth2AuthenticationFailureHandler));

        http
                .logout(logout->logout
                        .clearAuthentication(true));

        return http.build();
    }
}
