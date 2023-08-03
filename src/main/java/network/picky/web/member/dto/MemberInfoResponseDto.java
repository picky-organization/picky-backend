package network.picky.web.member.dto;

import lombok.Builder;
import lombok.Getter;
import network.picky.web.auth.enums.AuthProvider;
import network.picky.web.member.enums.Role;

@Getter
public class MemberInfoResponseDto {

    private final String email;

    private final String socialType;

    private final String picture;

    private final String name;

    private final String introduce;

    private final String field;

    private final String github;

    private final String facebook;

    private final String instagram;

    private final AuthProvider authProvider;

    private final Role role;

    private final int projectCount;

    private final int projectCommentCount;

    @Builder
    public MemberInfoResponseDto(String email, String socialType, String picture, String name, String introduce, String field, String github, String facebook, String instagram, AuthProvider authProvider, Role role, int projectCount, int projectCommentCount) {
        this.email = email;
        this.socialType = socialType;
        this.picture = picture;
        this.name = name;
        this.introduce = introduce;
        this.field = field;
        this.github = github;
        this.facebook = facebook;
        this.instagram = instagram;
        this.authProvider = authProvider;
        this.role = role;
        this.projectCount = projectCount;
        this.projectCommentCount = projectCommentCount;
    }

}
