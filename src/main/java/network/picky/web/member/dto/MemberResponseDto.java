package network.picky.web.member.dto;

import lombok.Builder;
import lombok.Getter;

public class MemberResponseDto {

    @Getter
    @Builder
    public static class TokenInfo {
        private String grantType;
        private String accessToken;
        private int accessTokenExpirationTime;
        private String refreshToken;
        private int refreshTokenExpirationTime;

        public TokenInfo(String grantType, String accessToken, int accessTokenExpirationTime, String refreshToken, int refreshTokenExpirationTime) {
            this.grantType = grantType;
            this.accessToken = accessToken;
            this.accessTokenExpirationTime = accessTokenExpirationTime;
            this.refreshToken = refreshToken;
            this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        }
    }


}
