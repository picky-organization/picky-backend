package network.picky.web.project.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import network.picky.web.member.dto.MemberSummaryResponseDto;
import network.picky.web.project.domain.ProjectComment;
@Getter
public class ProjectCommentResponseDto {
    @Positive @NotNull
    private Long id;
    @NotNull
    private MemberSummaryResponseDto memberSummaryResponseDto;
    @Positive @NotNull
    private Long projectId;
    @Null @Positive
    private Long parentId;
    @PositiveOrZero @NotNull
    private int childSize=0;
    @NotBlank @Size(max=2000)
    private String content;

    public ProjectCommentResponseDto(ProjectComment projectComment, int childSize){
        this.id = projectComment.getId();
        this.memberSummaryResponseDto = new MemberSummaryResponseDto(projectComment.getMember());
        this.projectId = projectComment.getProject().getId();
        if (projectComment.getParent() != null) {
            this.parentId = projectComment.getParent().getId();
        }
        this.childSize = childSize;
        this.content = projectComment.getContent();
    }
    public ProjectCommentResponseDto(ProjectComment projectComment){
        this.id = projectComment.getId();
        this.memberSummaryResponseDto = new MemberSummaryResponseDto(projectComment.getMember());
        this.projectId = projectComment.getProject().getId();
        if (projectComment.getParent() != null) {
            this.parentId = projectComment.getParent().getId();
        }
        this.content = projectComment.getContent();
    }
}
