package network.picky.web.project.repository;

import network.picky.web.member.domain.Member;
import network.picky.web.member.enums.Role;
import network.picky.web.member.repository.MemberRepository;
import network.picky.web.project.domain.Project;
import network.picky.web.project.domain.ProjectComment;
import network.picky.web.project.dto.ProjectSaveRequestDto;
import network.picky.web.project.enums.State;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.stream.LongStream;

@DataJpaTest
class ProjectCommentRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectCommentRepository projectCommentRepository;

    Member member;
    Project project;
    Project project2;

    int endSize = 5;

    @BeforeEach
    public void setUp() {
        member = Member.builder()
                .email("test@test.com")
                .name("test")
                .picture("https://tistory1.daumcdn.net/tistory/3095648/attach/ad5c70ba90d7493db85c371ffb9d0f89")
                .socialType("test")
                .role(Role.USER)
                .build();
        memberRepository.save(member);

        ProjectSaveRequestDto projectSaveRequestDto = ProjectSaveRequestDto.builder()
                .categories(null)
                .teches(null)
                .title("title")
                .content("content")
                .thumbnail("thumbnail")
                .state(State.PRODUCT)
                .website("website")
                .appstore("appstore")
                .playstore("playstore")
                .build();

        project = projectSaveRequestDto.toEntity(member);
        projectRepository.save(project);
        project2 = projectSaveRequestDto.toEntity(member);
        projectRepository.save(project2);
    }

    @BeforeEach
    public void cleanUp(){
        projectCommentRepository.deleteAll();
    }

    @Test
    void findAllByProjectAndParentIsNull() {
        //given
        List<ProjectComment> projectCommentList = LongStream.range(0,endSize).mapToObj(aLong -> new ProjectComment(member,project,null,"content"+aLong)).toList();
        projectCommentRepository.saveAll(projectCommentList);

        List<ProjectComment> projectCommentList2 = LongStream.range(0,endSize).mapToObj(aLong -> new ProjectComment(member,project2,null,"content"+aLong)).toList();
        projectCommentRepository.saveAll(projectCommentList);

        //when
        List<ProjectComment> findProjectCommentList = projectCommentRepository.findAllByProjectAndParentIsNull(project);
        //then
        Assertions.assertEquals(projectCommentList, findProjectCommentList);
    }

    @Test
    void findAllByParent() {
        //given
        List<ProjectComment> projectCommentList = LongStream.range(0,endSize).mapToObj(aLong -> new ProjectComment(member,project,null,"content"+aLong)).toList();
        projectCommentRepository.saveAll(projectCommentList);

        List<ProjectComment> projectCommentChildList = LongStream.range(0,endSize).mapToObj(aLong -> new ProjectComment(member,project,projectCommentList.get(0),"content"+aLong)).toList();
        projectCommentRepository.saveAll(projectCommentChildList);
        //when
        List<ProjectComment> findProjectCommentChildList = projectCommentRepository.findAllByParent(projectCommentList.get(0));
        //then
        Assertions.assertEquals(projectCommentChildList, findProjectCommentChildList);
    }

    @Test
    void countByParent() {
        //given
        List<ProjectComment> projectCommentList = LongStream.range(0,endSize).mapToObj(aLong -> new ProjectComment(member,project,null,"content"+aLong)).toList();
        projectCommentRepository.saveAll(projectCommentList);

        List<ProjectComment> projectCommentChildList = LongStream.range(0,endSize).mapToObj(aLong -> new ProjectComment(member,project,projectCommentList.get(0),"content"+aLong)).toList();
        projectCommentRepository.saveAll(projectCommentChildList);
        //when
        ProjectComment findComment = projectCommentRepository.findAllByProjectAndParentIsNull(project).get(0);
        int count = projectCommentRepository.countByParent(findComment);
        //then
        Assertions.assertEquals(count, endSize);
    }

}