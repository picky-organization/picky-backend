package network.picky.web.member.dto;

import lombok.Getter;
import network.picky.web.member.domain.Member;
@Getter
public class MemberSummaryResponseDto {

    private final Long id;

    private final String email;

    private final String picture;

    private final String name;

    public MemberSummaryResponseDto(Member member){
        this.id = member.getId();
        this.email = member.getEmail();
        this.picture = member.getPicture();
        this.name = member.getName();
    }

}
