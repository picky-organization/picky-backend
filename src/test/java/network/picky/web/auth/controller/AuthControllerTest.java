package network.picky.web.auth.controller;

import jakarta.servlet.http.Cookie;
import network.picky.web.auth.domain.SavedToken;
import network.picky.web.auth.dto.AuthUser;
import network.picky.web.auth.repository.SavedTokenRepository;
import network.picky.web.auth.token.JwtTokenProvider;
import network.picky.web.member.domain.Member;
import network.picky.web.member.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@Import({AuthController.class})
@WebMvcTest(controllers = AuthController.class, useDefaultFilters = false)
class AuthControllerTest {
        @Autowired
        private MockMvc mvc;

        @Autowired
        private JwtTokenProvider jwtTokenProvider;

        @MockBean
        private SavedTokenRepository refreshTokenRepository;

        @TestConfiguration
        static class AdditionalConfig {
            @Bean
            public JwtTokenProvider jwtTokenProvider() {
                return new JwtTokenProvider("k3@6b^kll7zd($@=la0_$7tiu+8kfl@gc4zabflp-0_k*!#3y", 100000, 100000);
            }
        }

        @Test
        @DisplayName("정상작동시")
        @WithMockUser
        public void testRefresh() throws Exception {
            // given
            String path = "/auth/refresh";

            Long id = 1L;
            AuthUser authUser = new AuthUser(id, Role.USER);
            String refreshToken = jwtTokenProvider.createRefreshToken(authUser);

            Mockito.when(refreshTokenRepository.findByRefreshToken(refreshToken)).thenReturn(new SavedToken(new Member(id), refreshToken));
            Cookie cookie = new Cookie("refresh_token", refreshToken);
            // when
            ResultActions ra = mvc.perform(get(path).cookie(cookie));

            // then
            ra.andExpect(status().isCreated());
            ra.andDo(result -> {
                String s = result.getResponse().getHeader(HttpHeaders.AUTHORIZATION).split(" ")[1];
                assert jwtTokenProvider.validToken(s);
            });
        }

    @Test
    @DisplayName("cookie가 없을때")
    @WithMockUser
    public void testRefreshNoCookie() throws Exception {
        // given
        String path = "/auth/refresh";
        // when
        ResultActions ra = mvc.perform(get(path));

        // then
        ra.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("refresh_token cookie is null")
    @WithMockUser
    public void testRefreshIsNull() throws Exception {
        // given
        String path = "/auth/refresh";
        Long id = 1L;
        Cookie cookie = new Cookie("refresh_token", "");
        // when
        ResultActions ra = mvc.perform(get(path).cookie(cookie));

        // then
        ra.andExpect(status().isBadRequest());
    }


}