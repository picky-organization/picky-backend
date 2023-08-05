package network.picky.web.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.auth.dto.AuthUser;
import network.picky.web.auth.jwt.token.JwtTokenProvider;
import network.picky.web.category.dto.CategorySaveRequestDto;
import network.picky.web.category.service.CategoryService;
import network.picky.web.member.domain.Member;
import network.picky.web.member.enums.Role;
import network.picky.web.member.repository.MemberRepository;
import network.picky.web.tech.dto.TechSaveRequestDto;
import network.picky.web.tech.service.TechService;
import org.springframework.context.annotation.Configuration;
@Slf4j
@RequiredArgsConstructor
@Configuration
public class InsertData {
    private final CategoryService categoryService;
    private final TechService techService;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @PostConstruct
    public void init(){
        categoryService.create(new CategorySaveRequestDto("category1"));
        categoryService.create(new CategorySaveRequestDto("category2"));
        categoryService.create(new CategorySaveRequestDto("category3"));

        techService.create(new TechSaveRequestDto("tech1"));
        techService.create(new TechSaveRequestDto("tech2"));
        techService.create(new TechSaveRequestDto("tech3"));

        Member member = Member.builder()
                .email("test@test.com")
                .name("test")
                .picture("https://tistory1.daumcdn.net/tistory/3095648/attach/ad5c70ba90d7493db85c371ffb9d0f89")
                .socialType("test")
                .role(Role.USER)
                .build();
        memberRepository.save(member);
        log.info(jwtTokenProvider.createRefreshToken(new AuthUser(member.getId(), member.getRole())));
    }
}
