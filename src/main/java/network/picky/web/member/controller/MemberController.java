package network.picky.web.member.controller;

import lombok.RequiredArgsConstructor;
import network.picky.web.auth.dto.AuthUser;
import network.picky.web.auth.jwt.token.Login;
import network.picky.web.member.dto.MemberInfoResponseDto;
import network.picky.web.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/info")
    public ResponseEntity<MemberInfoResponseDto> findMemberInfo(@Login AuthUser authUser) {
        MemberInfoResponseDto memberResponseDto = memberService.findMemberInfo(authUser);
        return ResponseEntity.ok(memberResponseDto);
    }

}
