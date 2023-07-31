package network.picky.web.project.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.common.domain.BaseEntity;
import network.picky.web.member.domain.Member;
import network.picky.web.project.dto.ProjectCommentUpdateRequestDto;

@Getter
@NoArgsConstructor
@Entity
public class ProjectComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne(optional = false)
    @JoinColumn(name="project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name="parent_id")
    private ProjectComment parent;

    @Column(nullable = false, length = 2000, columnDefinition="TEXT")
    private String content;

    @Builder
    public ProjectComment(Member member, Project project, ProjectComment parent, String content){
        this.member = member;
        this.project = project;
        this.parent = parent;
        this.content = content;
    }

    public void update(ProjectCommentUpdateRequestDto projectCommentUpdateRequestDto){
        this.content = projectCommentUpdateRequestDto.getContent();
    }

}
