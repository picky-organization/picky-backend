package network.picky.web.project.service;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.member.domain.Member;
import network.picky.web.member.enums.Role;
import network.picky.web.member.exception.MemberNotFoundException;
import network.picky.web.member.repository.MemberRepository;
import network.picky.web.project.domain.Project;
import network.picky.web.project.domain.ProjectComment;
import network.picky.web.project.dto.ProjectCommentCreateRequestDto;
import network.picky.web.project.dto.ProjectCommentResponseDto;
import network.picky.web.project.dto.ProjectCommentUpdateRequestDto;
import network.picky.web.project.enums.State;
import network.picky.web.project.exception.ProjectCommentBadRequestException;
import network.picky.web.project.exception.ProjectCommentNotFoundException;
import network.picky.web.project.exception.ProjectNotFoundException;
import network.picky.web.project.repository.ProjectCommentRepository;
import network.picky.web.project.repository.ProjectRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
class ProjectCommentServiceTest {
    @Autowired
    EntityManager entityManager;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectCommentRepository projectCommentRepository;
    ProjectCommentService projectCommentService;
    Member member;
    Project project;
    Project project2;

    @BeforeEach
    void setUp() {
        projectCommentService = new ProjectCommentService(memberRepository, projectRepository, projectCommentRepository);
        member = createMember();
        project = createProject();
        project2 = createProject();
    }

    @AfterEach
    void cleanUp() {
        projectCommentRepository.deleteAll();
        projectRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("readAll : 정상")
    void readAll() {
        //given
        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .content("comment")
                .build();

        ProjectComment projectComment2 = ProjectComment.builder()
                .member(member)
                .project(project)
                .content("comment2")
                .build();

        projectCommentRepository.save(projectComment);
        projectCommentRepository.save(projectComment2);

        //when
        List<ProjectCommentResponseDto> projectCommentResponseDtos = projectCommentService.readAll(project.getId(), null);

        //then
        assertEquals(projectCommentResponseDtos.get(0).getId(), projectComment.getId());
        assertEquals(projectCommentResponseDtos.get(0).getContent(), projectComment.getContent());
        assertEquals(projectCommentResponseDtos.get(0).getChildSize(), 0);

        assertEquals(projectCommentResponseDtos.get(1).getId(), projectComment2.getId());
        assertEquals(projectCommentResponseDtos.get(1).getContent(), projectComment2.getContent());
        assertEquals(projectCommentResponseDtos.get(1).getChildSize(), 0);
    }

    @Test
    @DisplayName("readAll : childSize 확인")
    void readAllChildSizeCheck() {
        //given
        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .content("comment")
                .build();
        projectCommentRepository.save(projectComment);

        ProjectComment child = ProjectComment.builder()
                .member(member)
                .project(project)
                .content("comment")
                .parent(projectComment)
                .build();
        projectCommentRepository.save(child);

        ProjectComment projectComment2 = ProjectComment.builder()
                .member(member)
                .project(project)
                .content("comment2")
                .build();

        projectCommentRepository.save(projectComment2);

        //when
        List<ProjectCommentResponseDto> projectCommentResponseDtos = projectCommentService.readAll(project.getId(), null);

        //then
        assertEquals(projectCommentResponseDtos.get(0).getId(), projectComment.getId());
        assertEquals(projectCommentResponseDtos.get(0).getContent(), projectComment.getContent());
        assertEquals(projectCommentResponseDtos.get(0).getChildSize(), 1);
        assertEquals(projectCommentResponseDtos.get(1).getId(), projectComment2.getId());
        assertEquals(projectCommentResponseDtos.get(1).getContent(), projectComment2.getContent());
        assertEquals(projectCommentResponseDtos.get(1).getChildSize(), 0);
    }


    @Test
    @DisplayName("readAll : parent를 null로 입력했을때 null인 것만 가져오는지 test")
    void readAllParentIsNull() {
        //given
        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .content("comment")
                .build();

        ProjectComment projectComment2 = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(projectComment)
                .content("comment2")
                .build();

        projectCommentRepository.save(projectComment);
        projectCommentRepository.save(projectComment2);

        //when
        List<ProjectCommentResponseDto> projectCommentResponseDtos = projectCommentService.readAll(project.getId(), null);

        //then
        assertEquals(projectCommentResponseDtos.size(), 1);
    }

    @Test
    @DisplayName("readAll : parent를 값을 입력했을때 해당하는 parent의 child comment만 가져오는지 테스트")
    void readAllParentAssign() {
        //given
        ProjectComment parent = ProjectComment.builder()
                .member(member)
                .project(project)
                .content("comment")
                .build();

        ProjectComment projectComment2 = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(parent)
                .content("comment2")
                .build();

        projectCommentRepository.save(parent);
        projectCommentRepository.save(projectComment2);

        //when
        List<ProjectCommentResponseDto> projectCommentResponseDtos = projectCommentService.readAll(project.getId(), parent.getId());

        //then
        assertEquals(projectCommentResponseDtos.size(), 1);
    }

    @Test
    @DisplayName("readAll : parent를 지정했지만 해당 parent가 존재하지 않을 때 가져오는지 테스트")
    void readAllParentNotExists() {
        //given
        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .content("comment")
                .build();

        ProjectComment projectComment2 = ProjectComment.builder()
                .member(member)
                .project(project)
                .content("comment2")
                .build();

        projectCommentRepository.save(projectComment);
        projectCommentRepository.save(projectComment2);

        long notExistsProjectCommentId = 0;
        //when
        Executable executable = () -> projectCommentService.readAll(project.getId(), notExistsProjectCommentId);

        //then
        assertThrows(ProjectCommentNotFoundException.class, executable);
    }

    @Test
    @DisplayName("readAll : project의 comment만 가져오는지 test")
    void readAlProjectComment() {
        //given
        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .content("comment")
                .build();
        projectCommentRepository.save(projectComment);

        ProjectComment project2Comment = ProjectComment.builder()
                .member(member)
                .project(project2)
                .content("comment2")
                .build();

        ProjectComment project2Comment2 = ProjectComment.builder()
                .member(member)
                .project(project2)
                .parent(projectComment)
                .content("comment3")
                .build();

        projectCommentRepository.save(project2Comment);
        projectCommentRepository.save(project2Comment2);

        //when
        List<ProjectCommentResponseDto> projectCommentResponseDtos = projectCommentService.readAll(project.getId(), null);

        //then
        assertEquals(projectCommentResponseDtos.size(), 1);
    }

    @Test
    @DisplayName("readAll : project가 존재하지 않지만 해당 project id로 요청할 때 test")
    void readAllProjectNotExists() {
        //given
        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .content("comment")
                .build();
        ProjectComment projectComment2 = ProjectComment.builder()
                .member(member)
                .project(project)
                .content("comment2")
                .build();
        ProjectComment projectComment3 = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(projectComment)
                .content("comment2")
                .build();

        projectCommentRepository.save(projectComment);
        projectCommentRepository.save(projectComment2);
        projectCommentRepository.save(projectComment3);

        long notExistsProjectId = 0;
        //when
        Executable executable = () -> projectCommentService.readAll(notExistsProjectId, null);

        //then
        assertThrows(ProjectNotFoundException.class, executable);
    }

    @Test
    @DisplayName("readAll : 결과가 비어 있을때")
    void readAllIsEmpty() {
        //given
        //when
        List<ProjectCommentResponseDto> projectCommentResponseDtos = projectCommentService.readAll(project.getId(), null);

        //then
        assertTrue(projectCommentResponseDtos.isEmpty());
    }

    @Test
    @DisplayName("create : parent가 null일때 테스트")
    void createParentIsNull() {
        //given
        ProjectCommentCreateRequestDto projectCommentCreateRequestDto = ProjectCommentCreateRequestDto.builder()
                .content("comment")
                .build();

        //when
        ProjectCommentResponseDto projectCommentResponseDto = projectCommentService.create(project.getId(), member.getId(), projectCommentCreateRequestDto);

        //then
        assertEquals(projectCommentResponseDto.getMemberSummaryResponseDto().getId(), member.getId());
        assertEquals(projectCommentResponseDto.getParentId(), null);
        assertEquals(projectCommentResponseDto.getProjectId(), project.getId());
        assertEquals(projectCommentResponseDto.getContent(), "comment");
    }

    @Test
    @DisplayName("create : parent가 존재할 때 test")
    void createParentExists() {
        //given
        ProjectComment parentComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(null)
                .content("comment2")
                .build();
        projectCommentRepository.save(parentComment);

        ProjectCommentCreateRequestDto projectCommentCreateRequestDto = ProjectCommentCreateRequestDto.builder()
                .parentId(parentComment.getId())
                .content("comment")
                .build();

        //when
        ProjectCommentResponseDto projectCommentResponseDto = projectCommentService.create(project.getId(), member.getId(), projectCommentCreateRequestDto);

        //then
        assertEquals(projectCommentResponseDto.getMemberSummaryResponseDto().getId(), member.getId());
        assertEquals(projectCommentResponseDto.getProjectId(), project.getId());
        assertEquals(projectCommentResponseDto.getParentId(), parentComment.getId());
        assertEquals(projectCommentResponseDto.getContent(), "comment");
    }

    @Test
    @DisplayName("create : parent를 입력했지만 존재하지 않을 떄 test")
    void createParentNotFound() {
        //given
        long notFoundParentId = 0;
        ProjectCommentCreateRequestDto projectCommentCreateRequestDto = ProjectCommentCreateRequestDto.builder()
                .parentId(notFoundParentId)
                .content("comment")
                .build();

        //when
        Executable executable = () -> projectCommentService.create(project.getId(), member.getId(), projectCommentCreateRequestDto);

        //then
        assertThrows(ProjectCommentNotFoundException.class, executable);
    }

    @Test
    @DisplayName("create : parent가 이미 child일때 test")
    void createParentIsAlreadyChild() {
        //given
        ProjectComment parentComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(null)
                .content("comment2")
                .build();
        projectCommentRepository.save(parentComment);
        ProjectComment parentComment2 = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(parentComment)
                .content("comment2")
                .build();
        projectCommentRepository.save(parentComment2);


        ProjectCommentCreateRequestDto projectCommentCreateRequestDto = ProjectCommentCreateRequestDto.builder()
                .parentId(parentComment2.getId())
                .content("comment")
                .build();

        //when
        Executable executable = () -> projectCommentService.create(project.getId(), member.getId(), projectCommentCreateRequestDto);

        //then
        assertThrows(ProjectCommentBadRequestException.class, executable);
    }

    @Test
    @DisplayName("create : member가 존재하지 않지만 요청할 때 테스트")
    void createMemberNotFound() {
        //given
        ProjectCommentCreateRequestDto projectCommentCreateRequestDto = ProjectCommentCreateRequestDto.builder()
                .content("comment")
                .build();

        long notFoundMemberId = 0;
        //when
        Executable executable = () -> projectCommentService.create(project.getId(), notFoundMemberId, projectCommentCreateRequestDto);

        //then
        assertThrows(MemberNotFoundException.class, executable);
    }

    @Test
    @DisplayName("create : project가 존재하지 않지만 요청할 때 테스트")
    void createProjectNotFound() {
        //given
        ProjectCommentCreateRequestDto projectCommentCreateRequestDto = ProjectCommentCreateRequestDto.builder()
                .content("comment")
                .build();

        long notFoundsProjectId = 0;
        //when
        Executable executable = () -> projectCommentService.create(notFoundsProjectId, member.getId(), projectCommentCreateRequestDto);

        //then
        assertThrows(ProjectNotFoundException.class, executable);
    }

    @Test
    @DisplayName("create : project의 comment count가 증가하는지 테스트")
    void createIncreaseProjectCommentCount() {
        //given
        ProjectCommentCreateRequestDto projectCommentCreateRequestDto = ProjectCommentCreateRequestDto.builder()
                .content("comment")
                .build();

        //when
        ProjectCommentResponseDto projectCommentResponseDto = projectCommentService.create(project.getId(), member.getId(), projectCommentCreateRequestDto);

        //then
        assertEquals(project.getCommentCount(), 1);
    }

    @Test
    @DisplayName("read : parent가 null 일때")
    void readParentIsNull() {
        //given
        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(null)
                .content("comment")
                .build();
        projectCommentRepository.save(projectComment);

        //when
        ProjectCommentResponseDto projectCommentResponseDto = projectCommentService.read(projectComment.getId());

        //then
        assertEquals(projectCommentResponseDto.getProjectId(), project.getId());
        assertEquals(projectCommentResponseDto.getMemberSummaryResponseDto().getId(), member.getId());
        assertEquals(projectCommentResponseDto.getParentId(), null);
        assertEquals(projectCommentResponseDto.getContent(), "comment");
        assertEquals(projectCommentResponseDto.getChildSize(), 0);
    }

    @Test
    @DisplayName("read : childSize 가 0이 아닐때")
    void readCheckChildSize() {
        //given
        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(null)
                .content("comment")
                .build();
        projectCommentRepository.save(projectComment);

        ProjectComment child = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(projectComment)
                .content("comment")
                .build();
        projectCommentRepository.save(child);

        //when
        ProjectCommentResponseDto projectCommentResponseDto = projectCommentService.read(projectComment.getId());

        //then
        assertEquals(projectCommentResponseDto.getProjectId(), project.getId());
        assertEquals(projectCommentResponseDto.getMemberSummaryResponseDto().getId(), member.getId());
        assertEquals(projectCommentResponseDto.getParentId(), null);
        assertEquals(projectCommentResponseDto.getContent(), "comment");
        assertEquals(projectCommentResponseDto.getChildSize(), 1);
    }

    @Test
    @DisplayName("read : parent가 null이 아닐때")
    void readParentExists() {
        //given
        ProjectComment parentComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(null)
                .content("commentParent")
                .build();
        projectCommentRepository.save(parentComment);

        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(parentComment)
                .content("comment")
                .build();
        projectCommentRepository.save(projectComment);

        //when
        ProjectCommentResponseDto projectCommentResponseDto = projectCommentService.read(projectComment.getId());

        //then
        assertEquals(projectCommentResponseDto.getProjectId(), project.getId());
        assertEquals(projectCommentResponseDto.getMemberSummaryResponseDto().getId(), member.getId());
        assertEquals(projectCommentResponseDto.getParentId(), parentComment.getId());
        assertEquals(projectCommentResponseDto.getContent(), "comment");
    }

    @Test
    @DisplayName("read : Not Found")
    void readNotFound() {
        //given
        long notFoundId = 0;
        //when
        Executable executable = () -> projectCommentService.read(notFoundId);

        //then
        assertThrows(ProjectCommentNotFoundException.class, executable);
    }

    @Test
    @DisplayName("update : 정상")
    void update() {
        //given
        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(null)
                .content("comment")
                .build();
        projectCommentRepository.save(projectComment);

        ProjectCommentUpdateRequestDto projectCommentUpdateRequestDto = ProjectCommentUpdateRequestDto.builder().content("update").build();

        //when
        ProjectCommentResponseDto projectCommentResponseDto = projectCommentService.update(projectComment.getId(), projectCommentUpdateRequestDto);

        //then
        assertEquals(projectComment.getId(), projectCommentResponseDto.getId());
        assertEquals(projectCommentResponseDto.getContent(), "update");
    }

    @Test
    @DisplayName("update : childSize check")
    void updateChildSizeCheck() {
        //given
        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(null)
                .content("comment")
                .build();
        projectCommentRepository.save(projectComment);

        ProjectComment child = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(projectComment)
                .content("comment")
                .build();
        projectCommentRepository.save(child);

        ProjectCommentUpdateRequestDto projectCommentUpdateRequestDto = ProjectCommentUpdateRequestDto.builder().content("update").build();

        //when
        ProjectCommentResponseDto projectCommentResponseDto = projectCommentService.update(projectComment.getId(), projectCommentUpdateRequestDto);

        //then
        assertEquals(projectComment.getId(), projectCommentResponseDto.getId());
        assertEquals(projectCommentResponseDto.getContent(), "update");
        assertEquals(projectCommentResponseDto.getChildSize(), 1);
    }

    @Test
    @DisplayName("update : content가 null일때")
    void updateContentFieldsIsNull() {
        //given
        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(null)
                .content("comment")
                .build();
        projectCommentRepository.save(projectComment);

        ProjectCommentUpdateRequestDto projectCommentUpdateRequestDto = ProjectCommentUpdateRequestDto.builder().content(null).build();

        //when
        Executable executable = () -> projectCommentService.update(projectComment.getId(), projectCommentUpdateRequestDto);

        //then
        assertThrows(DataIntegrityViolationException.class, executable);
        entityManager.clear();
    }

    @DisplayName("update : Not Found")
    void updateCommentNotFOund() {
        //given
        long notFoundCommentId = 0;

        ProjectCommentUpdateRequestDto projectCommentUpdateRequestDto = ProjectCommentUpdateRequestDto.builder().content("comment").build();

        //when
        Executable executable = () -> projectCommentService.update(notFoundCommentId, projectCommentUpdateRequestDto);

        //then
        assertThrows(ProjectCommentNotFoundException.class, executable);
    }

    @Test
    @DisplayName("delete : 정상")
    void delete() {
        //given
        ProjectComment projectComment = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(null)
                .content("comment")
                .build();
        projectCommentRepository.save(projectComment);
        //when
        projectCommentService.delete(projectComment.getId());
    }

    @Test
    @DisplayName("delete : Not Found")
    void deleteNotFound() {
        //given
        long notFoundCommentId = 0;

        //when
        Executable executable = () -> projectCommentService.delete(notFoundCommentId);

        //then
        assertThrows(ProjectCommentNotFoundException.class, executable);
    }

    @Test
    @DisplayName("delete : project comment count가 감소 하지는지 테스트")
    void deleteDecreaseProjectCommentCount() {
        //given
        ProjectCommentCreateRequestDto parent = ProjectCommentCreateRequestDto.builder()
                .content("comment")
                .build();
        ProjectCommentResponseDto comment = projectCommentService.create(project.getId(), member.getId(), parent);

        //when
        projectCommentService.delete(comment.getId());
        //then
        assertEquals(project.getCommentCount(), 0);
    }

    @Test
    @DisplayName("delete : parent comment 삭제시 project comment count가 여러개 감소 하지는지 테스트")
    void deleteParentCommentDecreaseProjectCommentCount() {
        //given
        ProjectCommentCreateRequestDto parent = ProjectCommentCreateRequestDto.builder()
                .content("comment")
                .build();
        ProjectCommentResponseDto parentDto = projectCommentService.create(project.getId(), member.getId(), parent);

        ProjectCommentCreateRequestDto child = ProjectCommentCreateRequestDto.builder()
                .content("comment")
                .parentId(parentDto.getId())
                .build();
        ProjectCommentResponseDto childDto = projectCommentService.create(project.getId(), member.getId(), child);
        //when
        projectCommentService.delete(parentDto.getId());
        //then
        assertEquals(project.getCommentCount(), 0);
    }

    Member createMember() {
        Member member = Member.builder()
                .email("test@test.com")
                .name("test")
                .picture("https://tistory1.daumcdn.net/tistory/3095648/attach/ad5c70ba90d7493db85c371ffb9d0f89")
                .socialType("test")
                .role(Role.USER)
                .build();
        return memberRepository.save(member);
    }

    Project createProject() {
        Project project = Project.builder()
                .member(member)
                .title("title")
                .content("content")
                .thumbnail("thumbnail")
                .state(State.PRODUCT)
                .website("website")
                .appstore("appstore")
                .playstore("playstore")
                .build();

        return projectRepository.save(project);
    }
}