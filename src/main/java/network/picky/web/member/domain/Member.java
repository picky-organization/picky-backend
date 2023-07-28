package network.picky.web.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.auth.dto.OAuth2UserInfo;
import network.picky.web.auth.enums.AuthProvider;
import network.picky.web.common.domain.BaseEntity;
import network.picky.web.member.enums.Role;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 50)
    private String oauth2Id;

    @Column(nullable = false, length = 2083)
    private String picture;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = true, length = 200)
    private String introduce;

    @Column(nullable = true, length = 20)
    private String field;

    @Column(nullable = true, length = 2083)
    private String github;

    @Column(nullable = true, length = 2083)
    private String facebook;

    @Column(nullable = true, length = 2083)
    private String instagram;

    @Column(nullable = true)
    private boolean commentNotice = true;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Enumerated(EnumType.STRING)
    private Role role;

    private int projectCount = 0;

    private int projectCommentCount = 0;

    @Builder
    public Member(String email, String oauth2Id, String picture, String name, String introduce, String field, String github, String facebook, String instagram, boolean commentNotice, AuthProvider authProvider, Role role, int projectCount, int projectCommentCount) {
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
    }

    public Member(Long id) {
        this.id = id;
    }

    public Member update(OAuth2UserInfo oAuth2UserInfo) {
        this.name = oAuth2UserInfo.getName();
        this.oauth2Id = oAuth2UserInfo.getOAuth2Id();

        return this;
    }

}
