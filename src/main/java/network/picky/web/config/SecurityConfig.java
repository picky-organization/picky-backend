package network.picky.web.config;

import lombok.RequiredArgsConstructor;
import network.picky.web.auth.cookie.CookieAuthorizationRequestRepository;
import network.picky.web.auth.handler.OAuth2AuthenticationFailureHandler;
import network.picky.web.auth.handler.OAuth2AuthenticationSuccessHandler;
import network.picky.web.auth.jwt.token.JwtAuthenticationFilter;
import network.picky.web.auth.service.CustomOAuth2UserService;
import network.picky.web.member.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableWebSecurity
@EnableTransactionManagement(order = 0)
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //httpBasic, csrf, formLogin, rememberMe, logout, session disable
        http
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .csrf(csrf -> csrf.disable())
                .rememberMe(rememberMe -> rememberMe.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //요청에 대한 권한 설정
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/member").hasRole(Role.USER.name())
                        .requestMatchers(HttpMethod.GET, "/category").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tech").permitAll()
                        .requestMatchers(HttpMethod.GET, "/project").permitAll()
                        .requestMatchers(HttpMethod.POST, "/project").hasRole(Role.USER.name())
                        .requestMatchers(HttpMethod.GET, "/project/{id}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/project/{id}").hasRole(Role.USER.name())
                        .requestMatchers(HttpMethod.DELETE, "/project/{id}").hasRole(Role.USER.name())
                        .requestMatchers(HttpMethod.GET, "/project/{projectId}/comment").permitAll()
                        .requestMatchers(HttpMethod.POST, "/project/{projectId}/comment").hasRole(Role.USER.name())
                        .requestMatchers(HttpMethod.GET, "/project/{projectId}/comment/{id}").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/project/{projectId}/comment/{id}").hasRole(Role.USER.name())
                        .requestMatchers(HttpMethod.DELETE, "/project/{projectId}/comment/{id}").hasRole(Role.USER.name())
                        .anyRequest().authenticated()
                )
                .exceptionHandling(handle -> handle.authenticationEntryPoint(new BasicAuthenticationEntryPoint()));

        //oauth2Login
        http
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(endpoint->endpoint
                                .baseUri("/oauth2/authorization")
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*"))
                        .loginPage("/auth/oauth")
                        .userInfoEndpoint(info -> info
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler));

        http
                .logout(logout -> logout
                        .clearAuthentication(true));

        http.addFilterAfter(jwtAuthenticationFilter, LogoutFilter.class);

        return http.build();
    }


    @Bean
    static RoleHierarchy roleHierarchy() {
        var hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(Role.ADMIN.getKey()+">"+Role.MANAGER.getKey()+">"+Role.USER.getKey());
        return hierarchy;
    }
}
