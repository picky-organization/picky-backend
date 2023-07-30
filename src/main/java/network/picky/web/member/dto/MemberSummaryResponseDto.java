package network.picky.web.member.dto;

import lombok.Getter;
import network.picky.web.member.domain.Member;
@Getter
public class MemberSummaryResponseDto {
    private Long id;
    private String email;
    private String picture;
    private String name;

    public MemberSummaryResponseDto(Member member){
        this.id = member.getId();
        this.email = member.getEmail();
        this.picture = member.getPicture();
        this.name = member.getName();
    }
}
