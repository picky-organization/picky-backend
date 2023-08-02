package network.picky.web.project.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import network.picky.web.member.domain.Member;
import network.picky.web.project.domain.Project;
import network.picky.web.project.domain.ProjectComment;

@Getter
@Builder
public class ProjectCommentCreateRequestDto {
    @Null @Positive
    private Long parentId;
    @NotNull @Size(max = 2000)
    private String content;

    public ProjectComment toEntity(Member member, Project project, ProjectComment parent){
        return ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(parent)
                .content(this.content)
                .build();
    }
}
