package network.picky.web.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import network.picky.web.member.domain.Member;
import network.picky.web.project.domain.Project;
import network.picky.web.project.enums.State;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Builder
@Getter
public class ProjectSaveRequestDto {
    @Size(min = 1)
    private List<Long> categories;
    private List<Long> teches;

    @URL
    @NotBlank
    @Size(max = 2083)
    private String thumbnail;
    @NotBlank
    @Size(max=100)
    private String title;
    @Size(min = 1, max= 65535)
    @NotBlank
    private String content;
    @NotNull
    private State state;
    @URL
    @Size(max = 2083)
    private String website;
    @URL
    @Size(max = 2083)
    private String appstore;
    @URL
    @Size(max = 2083)
    private String playstore;

    public Project toEntity(Member member){
        return Project.builder()
                .member(member)
                .thumbnail(this.thumbnail)
                .title(this.title)
                .content(this.content)
                .state(this.state)
                .website(this.website)
                .appstore(this.appstore)
                .playstore(this.playstore)
                .build();
    }
}
