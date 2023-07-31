package network.picky.web.project.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.common.domain.BaseEntity;
import network.picky.web.member.domain.Member;
import network.picky.web.project.dto.ProjectSaveRequestDto;
import network.picky.web.project.enums.State;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Project extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private List<ProjectCategory> projectCategories;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private List<ProjectTech> projectTeches;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private List<ProjectComment> projectComments;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false,  length = 65535, columnDefinition="TEXT")
    private String content;

    @Column(nullable = false, length = 2083)
    private String thumbnail;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(length = 2083)
    private String website;

    @Column(length = 2083)
    private String appstore;

    @Column(length = 2083)
    private String playstore;

    @Column(nullable = false)
    private int commentCount=0;

    @Column(nullable = false)
    private int likeCount=0;

    @Column(nullable = false)
    private int viewCount=0;

    @Builder
    public Project(Member member, String title, String content, String thumbnail, State state, String website, String appstore, String playstore) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.state = state;
        this.website = website;
        this.appstore = appstore;
        this.playstore = playstore;
    }

    public void updateProjectCategories(List<ProjectCategory> projectCategories) {
        this.projectCategories = projectCategories;
    }

    public void updateProjectTeches(List<ProjectTech> projectTeches){
        this.projectTeches = projectTeches;
    }

    public void update(ProjectSaveRequestDto projectSaveRequestDto){
        this.title = projectSaveRequestDto.getTitle();
        this.content = projectSaveRequestDto.getContent();
        this.thumbnail = projectSaveRequestDto.getThumbnail();
        this.state = projectSaveRequestDto.getState();
        this.website = projectSaveRequestDto.getWebsite();
        this.appstore = projectSaveRequestDto.getAppstore();
        this.playstore = projectSaveRequestDto.getPlaystore();
    }

    public void increaseViewCount(){
        this.viewCount++;
    }
    public void increaseCommentCount(){this.commentCount++;}
    public void decreaseCommentCount(){this.commentCount--;}
    public void decreaseCommentCount(int count){this.commentCount-=count;}
}
