package network.picky.web.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.auth.dto.OAuth2UserInfo;
import network.picky.web.auth.enums.AuthProvider;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String oauth2Id;

    private String picture;

    private String name;

    private String introduce;

    private String field;

    private String github;

    private String facebook;

    private String instagram;

    private String commentNotice;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Enumerated(EnumType.STRING)
    private Role role;

    private int projectCount;

    private int projectCommentCount;

    private LocalDateTime createDateTime;

    private LocalDateTime modifiedDataTime;

    @Builder
    public Member(String email, String oauth2Id, String picture, String name, String introduce, String field, String github, String facebook, String instagram, String commentNotice, AuthProvider authProvider, Role role, int projectCount, int projectCommentCount, LocalDateTime createDateTime, LocalDateTime modifiedDataTime) {
        this.email = email;
        this.oauth2Id = oauth2Id;
        this.picture = picture;
        this.name = name;
        this.introduce = introduce;
        this.field = field;
        this.github = github;
        this.facebook = facebook;
        this.instagram = instagram;
        this.commentNotice = commentNotice;
        this.authProvider = authProvider;
        this.role = role;
        this.projectCount = projectCount;
        this.projectCommentCount = projectCommentCount;
        this.createDateTime = createDateTime;
        this.modifiedDataTime = modifiedDataTime;
    }

    public Member update(OAuth2UserInfo oAuth2UserInfo) {
        this.name = oAuth2UserInfo.getName();
        this.oauth2Id = oAuth2UserInfo.getOAuth2Id();

        return this;
    }

}
