package network.picky.web.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private String socialType;

    @Column(nullable = false, length = 2083)
    private String picture;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(length = 200)
    private String introduce;

    @Column(length = 20)
    private String field;

    @Column(length = 2083)
    private String github;

    @Column(length = 2083)
    private String facebook;

    @Column(length = 2083)
    private String instagram;

    @Column(nullable = false)
    private boolean commentNotice = true;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private int projectCount = 0;

    private int projectCommentCount = 0;

    @Builder
    public Member(String email, String socialType, String picture, String name, String introduce, String field, String github, String facebook, String instagram, boolean commentNotice, AuthProvider authProvider, Role role, int projectCount, int projectCommentCount) {
        this.email = email;
        this.socialType = socialType;
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

    public void increaseProjectCount(){
        this.projectCount++;
    }

    public void decreaseProjectCount(){
        this.projectCount--;
    }

}
