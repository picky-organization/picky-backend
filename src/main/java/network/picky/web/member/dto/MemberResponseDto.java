package network.picky.web.member.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberResponseDto {

    @Getter
    @Builder
    public static class TokenInfo {
        private String grantType;
        private String accessToken;
        private Long accessTokenExpirationTime;
        private String refreshToken;
        private Long refreshTokenExpirationTime;

        public TokenInfo(String grantType, String accessToken, Long accessTokenExpirationTime, String refreshToken, Long refreshTokenExpirationTime) {
            this.grantType = grantType;
            this.accessToken = accessToken;
            this.accessTokenExpirationTime = accessTokenExpirationTime;
            this.refreshToken = refreshToken;
            this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        }
    }


}
