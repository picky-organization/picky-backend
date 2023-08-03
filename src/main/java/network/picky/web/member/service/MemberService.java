package network.picky.web.member.service;

import lombok.RequiredArgsConstructor;
import network.picky.web.auth.dto.AuthUser;
import network.picky.web.member.domain.Member;
import network.picky.web.member.dto.MemberInfoResponseDto;
import network.picky.web.member.exception.MemberNotFoundException;
import network.picky.web.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberInfoResponseDto findMemberInfo(AuthUser authUser) {

        Member findMember = memberRepository.findById(authUser.getId())
                .orElseThrow(MemberNotFoundException::new);

        return MemberInfoResponseDto.builder()
                .email(findMember.getEmail())
                .socialType(findMember.getSocialType())
                .picture(findMember.getPicture())
                .name(findMember.getName())
                .introduce(findMember.getIntroduce())
                .field(findMember.getField())
                .facebook(findMember.getFacebook())
                .github(findMember.getGithub())
                .instagram(findMember.getInstagram())
                .authProvider(findMember.getAuthProvider())
                .projectCount(findMember.getProjectCount())
                .projectCommentCount(findMember.getProjectCommentCount())
                .role(findMember.getRole())
                .build();
    }

}
