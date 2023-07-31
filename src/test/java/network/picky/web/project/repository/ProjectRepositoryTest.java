package network.picky.web.project.repository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.member.domain.Member;
import network.picky.web.member.enums.Role;
import network.picky.web.member.repository.MemberRepository;
import network.picky.web.project.domain.Project;
import network.picky.web.project.dto.ProjectSaveRequestDto;
import network.picky.web.project.enums.State;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@DataJpaTest
@Transactional
class ProjectRepositoryTest {
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("findAll : 정상 작동시")
    void findAll() {
        //given
        Member member = Member.builder()
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
        Project project = projectSaveRequestDto.toEntity(member);
        projectRepository.save(project);

        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<Project> projectPage = projectRepository.findAll(pageable);

        //then
        assertEquals(projectPage.getNumber(), 0);
        assertEquals(projectPage.getSize(), 10);
        assertEquals(projectPage.getTotalElements(), 1);
        assertEquals(projectPage.getContent().get(0), project);
    }

    @Test
    @DisplayName("findAll : 프로젝트가 없을때")
    void findAllEmpty() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<Project> projectPage = projectRepository.findAll(pageable);

        //then
        assertEquals(projectPage.getNumber(), 0);
        assertEquals(projectPage.getSize(), 10);
        assertEquals(projectPage.getTotalElements(), 0);
        assertEquals(projectPage.getContent(), List.of());
    }

    @Test
    @DisplayName("findAll : request가 잘못되었을떄")
    void findAllPageableIsNull() {
        //given
        Pageable pageable = null;

        //when
        Executable excutable = ()->projectRepository.findAll(pageable);

        //then
        assertThrows(NullPointerException.class,excutable);
    }
}
