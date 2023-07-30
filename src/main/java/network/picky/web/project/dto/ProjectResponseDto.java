package network.picky.web.project.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import network.picky.web.category.dto.CategoryResponseDto;
import network.picky.web.member.dto.MemberSummaryResponseDto;
import network.picky.web.project.domain.Project;
import network.picky.web.project.enums.State;
import network.picky.web.tech.dto.TechResponseDto;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProjectResponseDto {
    @NotNull
    private MemberSummaryResponseDto memberSummary;
    @Size(min = 1)
    @NotNull
    private List<CategoryResponseDto> categories;
    @Nullable
    private List<TechResponseDto> teches;
    @Positive
    @NotNull
    private Long id;
    @Size(max = 100)
    @NotBlank
    private String title;
    @Size(max= 65535)
    @NotBlank
    private String content;
    @Size(max= 2083)
    @NotBlank
    private String thumbnail;
    @NotNull
    private State state;
    @Size(max = 2083)
    @Nullable
    private String website;
    @Size(max = 2083)
    @Nullable
    private String appstore;
    @Size(max = 2083)
    @Nullable
    private String playstore;
    @PositiveOrZero
    @NotNull
    private int commentCount;
    @PositiveOrZero
    @NotNull
    private int likeCount;
    @PositiveOrZero
    @NotNull
    private int viewCount;

    public ProjectResponseDto(Project project){
        this.memberSummary = new MemberSummaryResponseDto(project.getMember());
        this.categories = project.getProjectCategories().stream().map(CategoryResponseDto::new).collect(Collectors.toList());
        this.teches = project.getProjectTeches().stream().map(TechResponseDto::new).collect(Collectors.toList());
        this.id = project.getId();
        this.title = project.getTitle();
        this.content = project.getContent();
        this.thumbnail = project.getThumbnail();
        this.state = project.getState();
        this.website = project.getWebsite();
        this.appstore = project.getAppstore();
        this.playstore = project.getPlaystore();
        this.commentCount = project.getCommentCount();
        this.likeCount = project.getLikeCount();
        this.viewCount = project.getViewCount();
    }

}
