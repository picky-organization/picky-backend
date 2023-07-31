package network.picky.web.project.service;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.category.domain.Category;
import network.picky.web.category.exception.CategoryNotFoundException;
import network.picky.web.category.repository.CategoryRepository;
import network.picky.web.member.domain.Member;
import network.picky.web.member.enums.Role;
import network.picky.web.member.repository.MemberRepository;
import network.picky.web.project.dto.ProjectAllResponseDto;
import network.picky.web.project.dto.ProjectResponseDto;
import network.picky.web.project.dto.ProjectSaveRequestDto;
import network.picky.web.project.enums.State;
import network.picky.web.project.exception.ProjectBadRequestException;
import network.picky.web.project.exception.ProjectNotFoundException;
import network.picky.web.project.repository.ProjectCategoryRepository;
import network.picky.web.project.repository.ProjectRepository;
import network.picky.web.project.repository.ProjectTechRepository;
import network.picky.web.tech.domain.Tech;
import network.picky.web.tech.exception.TechNotFoundException;
import network.picky.web.tech.repository.TechRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
class ProjectServiceTest {
    @Autowired
    EntityManager entityManager;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    TechRepository techRepository;
    @Autowired
    ProjectCategoryRepository projectCategoryRepository;
    @Autowired
    ProjectTechRepository projectTechRepository;
    ProjectService projectService;
    Member member;
    List<Category> categories;
    List<Tech> teches;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(memberRepository, projectRepository, categoryRepository, techRepository, projectCategoryRepository, projectTechRepository);
        member = createMember();
        categories = createCategories();
        teches = createTeches();
    }

    @AfterEach
    void cleanUp(){
        memberRepository.deleteAll();
        projectRepository.deleteAll();
        categoryRepository.deleteAll();
        techRepository.deleteAll();
    }

    @Test
    @DisplayName("readAll : 정상")
    void readAll() {
        //given
        appendProject();
        appendProject();
        appendProject();

        Pageable pageable = PageRequest.ofSize(20);
        //when
        Page<ProjectAllResponseDto> page = projectService.readAll(pageable);

        //then
        assertEquals(page.getTotalElements(), 3);
    }

    @Test
    @DisplayName("readAll : 결과가 비어 있을때")
    void readAllIsEmpty() {
        //given
        Pageable pageable = PageRequest.ofSize(20);

        //when
        Page<ProjectAllResponseDto> page = projectService.readAll(pageable);

        //then
        assertEquals(page.getTotalElements(), 0);
    }

    @Test
    @DisplayName("create : 정상")
    void create() {
        //given
        ProjectSaveRequestDto projectSaveRequestDto = ProjectSaveRequestDto.builder()
                .title("title")
                .content("content")
                .thumbnail("thumbnail")
                .state(State.PRODUCT)
                .website("website")
                .appstore("appstore")
                .playstore("playstore")
                .categories(categoryToLongList(categories))
                .teches(techToLongList(teches))
                .build();

        //when
       ProjectResponseDto projectResponseDto = projectService.create(member.getId(), projectSaveRequestDto);

       //then
        assertEquals(projectResponseDto.getTitle(), projectSaveRequestDto.getTitle());
        assertEquals(projectResponseDto.getContent(), projectSaveRequestDto.getContent());
        assertEquals(projectResponseDto.getThumbnail(), projectSaveRequestDto.getThumbnail());
        assertEquals(projectResponseDto.getState(), projectSaveRequestDto.getState());
        assertEquals(projectResponseDto.getWebsite(), projectSaveRequestDto.getWebsite());
        assertEquals(projectResponseDto.getAppstore(), projectSaveRequestDto.getAppstore());
        assertEquals(projectResponseDto.getPlaystore(), projectSaveRequestDto.getPlaystore());
    }

    @Test
    @DisplayName("create : member project count 증가 테스트")
    void createIncreaseMemberProjectCount() {
        //given
        ProjectSaveRequestDto projectSaveRequestDto = ProjectSaveRequestDto.builder()
                .title("title")
                .content("content")
                .thumbnail("thumbnail")
                .state(State.PRODUCT)
                .website("website")
                .appstore("appstore")
                .playstore("playstore")
                .categories(categoryToLongList(categories))
                .teches(techToLongList(teches))
                .build();
        //when
        projectService.create(member.getId(), projectSaveRequestDto);
        //then
        assertEquals(member.getProjectCount(), 1);
    }

    @Test
    @DisplayName("create : 카테고리가 null 일때")
    void createCategoryEmpty() {
        //given
        ProjectSaveRequestDto projectSaveRequestDto = ProjectSaveRequestDto.builder()
                .title("title")
                .content("content")
                .thumbnail("thumbnail")
                .state(State.PRODUCT)
                .website("website")
                .appstore("appstore")
                .playstore("playstore")
                .teches(techToLongList(teches))
                .build();

        //when
        Executable executable = ()-> projectService.create(member.getId(), projectSaveRequestDto);

        //then
        assertThrows(ProjectBadRequestException.class, executable);
    }

    @Test
    @DisplayName("create : 카테고리를 찾을 수 없을때")
    void createCategoryNotFound() {
        //given
        ProjectSaveRequestDto projectSaveRequestDto = ProjectSaveRequestDto.builder()
                .title("title")
                .content("content")
                .thumbnail("thumbnail")
                .state(State.PRODUCT)
                .website("website")
                .appstore("appstore")
                .playstore("playstore")
                .categories(List.of(Long.MAX_VALUE))
                .teches(techToLongList(teches))
                .build();

        //when
        Executable executable = ()-> projectService.create(member.getId(), projectSaveRequestDto);

        //then
        assertThrows(CategoryNotFoundException.class, executable);
    }

    @Test
    @DisplayName("create : 기술이 null 일때")
    void createTechEmpty() {
        //given
        ProjectSaveRequestDto projectSaveRequestDto = ProjectSaveRequestDto.builder()
                .title("title")
                .content("content")
                .thumbnail("thumbnail")
                .state(State.PRODUCT)
                .website("website")
                .appstore("appstore")
                .playstore("playstore")
                .categories(categoryToLongList(categories))
                .build();

        //when
        ProjectResponseDto projectResponseDto =  projectService.create(member.getId(), projectSaveRequestDto);

        //then
        assertEquals(projectResponseDto.getTitle(), projectSaveRequestDto.getTitle());
        assertEquals(projectResponseDto.getContent(), projectSaveRequestDto.getContent());
        assertEquals(projectResponseDto.getThumbnail(), projectSaveRequestDto.getThumbnail());
        assertEquals(projectResponseDto.getState(), projectSaveRequestDto.getState());
        assertEquals(projectResponseDto.getWebsite(), projectSaveRequestDto.getWebsite());
        assertEquals(projectResponseDto.getAppstore(), projectSaveRequestDto.getAppstore());
        assertEquals(projectResponseDto.getPlaystore(), projectSaveRequestDto.getPlaystore());
        assertEquals(projectResponseDto.getTeches(), Collections.emptyList());
    }


    @Test
    @DisplayName("create : 기술을 찾을 수 없을떄")
    void createTechNotFound() {
        //given
        ProjectSaveRequestDto projectSaveRequestDto = ProjectSaveRequestDto.builder()
                .title("title")
                .content("content")
                .thumbnail("thumbnail")
                .state(State.PRODUCT)
                .website("website")
                .appstore("appstore")
                .playstore("playstore")
                .categories(categoryToLongList(categories))
                .teches(List.of(Long.MAX_VALUE))
                .build();

        //when
        Executable executable = ()-> projectService.create(member.getId(), projectSaveRequestDto);

        //then
        assertThrows(TechNotFoundException.class, executable);
    }

    @Test
    @DisplayName("create : 몇몇 필수값이 null일때")
    void createTechStateEmpty() {
        //given
        ProjectSaveRequestDto projectSaveRequestDto = ProjectSaveRequestDto.builder()
                .title("title")
                .content("content")
                //.tumbnail("thumbnail")
                //.state(State.PRODUCT)
                .categories(categoryToLongList(categories))
                .build();

        //when
        Executable executable = ()-> projectService.create(member.getId(), projectSaveRequestDto);

        //then
        assertThrows(DataIntegrityViolationException.class, executable);
        entityManager.clear();
    }

    @Test
    @DisplayName("read : 정상")
    void read() {
        //given
        ProjectResponseDto project = appendProject();

        //when
        ProjectResponseDto readProject = projectService.read(project.getId());

        //then
        assertEquals(project.getId(), readProject.getId());
    }

    @Test
    @DisplayName("read : view count 증가 테스트")
    void readViewCount() {
        //given
        ProjectResponseDto project = appendProject();

        //when
        ProjectResponseDto readProject = projectService.read(project.getId());

        //then
        assertEquals(readProject.getViewCount(), 1);
    }



    @Test
    @DisplayName("read : Not Found")
    void readNotFound() {
        //given
        //when
        Executable executable = ()->projectService.read(0L);

        //then
        assertThrows(ProjectNotFoundException.class, executable);
    }

    @Test
    @DisplayName("update : 정상")
    void update() {
        //given
        ProjectResponseDto project = appendProject();
        List<Category> newCategories = createCategories(10,20);
        List<Tech> newTeches = createTeches(10,20);
        ProjectSaveRequestDto request = ProjectSaveRequestDto.builder()
                .title("new_title")
                .content("new_content")
                .thumbnail("new_thumbnail")
                .state(State.STOP)
                .website("new_website")
                .appstore("new_appstore")
                .playstore("new_playstore")
                .categories(categoryToLongList(newCategories))
                .teches(techToLongList(newTeches))
                .build();

        //when
        ProjectResponseDto update = projectService.update(project.getId(), request);

        //then
        assertEquals(project.getId(), update.getId());
        assertEquals(request.getTitle(), update.getTitle());
        assertEquals(request.getContent(), update.getContent());
        assertEquals(request.getState(), update.getState());
        assertEquals(request.getWebsite(), update.getWebsite());
        assertEquals(request.getAppstore(), update.getAppstore());
        assertEquals(request.getPlaystore(), update.getPlaystore());
        assertEquals(request.getCategories(), categoryToLongList(newCategories));
        assertEquals(request.getTeches(), techToLongList(newTeches));
        projectRepository.flush();
    }

    @Test
    @DisplayName("update : 몇몇 필수값이 null 일때")
    void updateSomFieldsNull() {
        //given
        ProjectResponseDto project = appendProject();
        List<Category> newCategories = createCategories(10,20);
        List<Tech> newTeches = createTeches(10,20);
        ProjectSaveRequestDto request = ProjectSaveRequestDto.builder()
                .title("new_title")
                .content("new_content")
                //.thumbnail("new_thumbnail")
                //.state(State.PRODUCT)
                .categories(categoryToLongList(newCategories))
                .teches(techToLongList(newTeches))
                .build();

        //when
        Executable executable = ()->{
            projectService.update(project.getId(), request);
            projectRepository.flush();
        };

        //then
        assertThrows(DataIntegrityViolationException.class, executable);
        entityManager.clear();
    }
    @Test
    @DisplayName("update : 카테고리가 null 일때")
    void updateCategoryNull() {
        //given
        ProjectResponseDto project = appendProject();
        List<Tech> newTeches = createTeches(10,20);
        ProjectSaveRequestDto request = ProjectSaveRequestDto.builder()
                .title("title")
                .content("content")
                .thumbnail("thumbnail")
                .state(State.PRODUCT)
                .website("website")
                .appstore("appstore")
                .playstore("playstore")
                .teches(techToLongList(newTeches))
                .build();

        //when
        Executable executable = ()->{
            projectService.update(project.getId(), request);
            projectRepository.flush();
        };

        //then
        assertThrows(ProjectBadRequestException.class, executable);
        entityManager.clear();
    }

    @Test
    @DisplayName("update : 카테고리를 찾을 수 없을떄")
    void updateCategoryNotFound() {
        //given
        ProjectResponseDto project = appendProject();
        List<Tech> newTeches = createTeches(10,20);
        ProjectSaveRequestDto request = ProjectSaveRequestDto.builder()
                .title("title")
                .content("content")
                .thumbnail("thumbnail")
                .state(State.PRODUCT)
                .website("website")
                .appstore("appstore")
                .playstore("playstore")
                .categories(List.of(Long.MAX_VALUE))
                .teches(techToLongList(newTeches))
                .build();

        //when
        Executable executable = ()->{
            projectService.update(project.getId(), request);
            projectRepository.flush();
        };

        //then
        assertThrows(CategoryNotFoundException.class, executable);
        entityManager.clear();
    }

    @Test
    @DisplayName("update : 기술을 찾을 수 없을 때")
    void updateTechNotFound() {
        //given
        ProjectResponseDto project = appendProject();
        List<Category> newCategories = createCategories(10,20);
        ProjectSaveRequestDto request = ProjectSaveRequestDto.builder()
                .title("title")
                .content("content")
                .thumbnail("thumbnail")
                .state(State.PRODUCT)
                .website("website")
                .appstore("appstore")
                .playstore("playstore")
                .categories(categoryToLongList(newCategories))
                .teches(List.of(Long.MAX_VALUE))
                .build();

        //when
        Executable executable = ()->{
            projectService.update(project.getId(), request);
            projectRepository.flush();
        };

        //then
        assertThrows(TechNotFoundException.class, executable);
        entityManager.clear();
    }


    @Test
    @DisplayName("delete : 정상")
    void delete() {
        //given
        ProjectResponseDto project = appendProject();
        //when
        projectService.delete(project.getId());
        //then
        Executable ex = ()-> projectService.read(project.getId());
        assertThrows(ProjectNotFoundException.class, ex);
    }

    @Test
    @DisplayName("delete : 연관된 카테고리가 삭제 되었는지 테스트")
    void deleteTestRelatedEntityDeleted() {
        //given
        ProjectResponseDto project = appendProject();
        //when
        projectService.delete(project.getId());
        //then
        assertEquals(projectCategoryRepository.findAll().size(),0);
        assertEquals(projectTechRepository.findAll().size(), 0);
    }

    @Test
    @DisplayName("delete : 프로젝트가 존재하지 않을때")
    void deleteProjectNotFound() {
        //given
        //when
        Executable ex = ()-> projectService.delete(0L);
        //then
        assertThrows(ProjectNotFoundException.class, ex);
    }

    @Test
    @DisplayName("delete : 프로젝트생성과 삭제후 member의 project count 변호 테스트")
    void deleteDecreaseProjectCount() {
        ProjectResponseDto project = appendProject();
        int nowCount = member.getProjectCount();
        //when
        projectService.delete(project.getId());
        //then
        assertTrue(nowCount>member.getProjectCount());
    }

    @Test
    @DisplayName("findAndValidCateories : 정상")
    void findAndValidCategories() {
        List<Category> findCategories = projectService.findAndValidCategories(categories.stream().map(category -> category.getId()).collect(Collectors.toList()));
        //when
        assertEquals(findCategories, categories);
    }


    @Test
    @DisplayName("findAndValidCateories : 카테고리를 찾을 수 없을때")
    void findAndValidCategoriesTestCategoryNotFound() {
        Executable executable = ()->projectService.findAndValidCategories(List.of(Long.MAX_VALUE));
        //when
        assertThrows(CategoryNotFoundException.class, executable);
    }


    @Test
    @DisplayName("findAndValidCateories : 파라미터가 null 일때")
    void findAndValidCategoriesTestParameterIsNull() {
        Executable executable = ()->projectService.findAndValidCategories(null);
        //when
        assertThrows(ProjectBadRequestException.class, executable);
    }

    @Test
    @DisplayName("findAndValidTeches : 정상")
    void findAndValidTeches() {
        List<Tech> findTeches = projectService.findAndValidTeches(teches.stream().map(tech -> tech.getId()).collect(Collectors.toList()));
        //when
        assertEquals(findTeches, teches);
    }


    @Test
    @DisplayName("findAndValidTeches : 기술을 찾을 수 없을때")
    void findAndValidTechesTestTechNotFound() {
        Executable executable = ()->projectService.findAndValidTeches(List.of(Long.MAX_VALUE));
        //when
        assertThrows(TechNotFoundException.class, executable);
    }


    @Test
    @DisplayName("findAndValidTeches : 파라미터가 null 일때")
    void findAndValidTechesTestParameterIsNull() {
        List<Tech> list =  projectService.findAndValidTeches(null);
        //when
        assertTrue(list.isEmpty());
    }


    Member createMember(){
         Member member = Member.builder()
                .email("test@test.com")
                .name("test")
                .picture("https://tistory1.daumcdn.net/tistory/3095648/attach/ad5c70ba90d7493db85c371ffb9d0f89")
                .socialType("test")
                .role(Role.USER)
                .build();
        return memberRepository.save(member);
    }

    List<Category> createCategories(int start, int end){
        List<Category> categories = LongStream.range(start, end).boxed()
                .map(aLong ->  Category.builder().name("category"+aLong).build()).toList();
        return categoryRepository.saveAll(categories);
    }

    List<Category> createCategories(){
        List<Category> categories = LongStream.range(1, 5).boxed()
                .map(aLong ->  Category.builder().name("category"+aLong).build()).toList();
        return categoryRepository.saveAll(categories);
    }

    List<Long> categoryToLongList(List<Category> categories){
        return categories.stream().map(Category::getId).toList();
    }

    List<Tech> createTeches(int start, int end){
        List<Tech> teches = LongStream.range(start, end).boxed()
                .map(aLong ->  Tech.builder().name("tech"+aLong).build()).toList();
        return techRepository.saveAll(teches);
    }


    List<Tech> createTeches(){
        List<Tech> teches = LongStream.range(1, 5).boxed()
                .map(aLong ->  Tech.builder().name("tech"+aLong).build()).toList();
        return techRepository.saveAll(teches);
    }

    List<Long> techToLongList(List<Tech> teches){
        return teches.stream().map(Tech::getId).toList();
    }

    ProjectResponseDto appendProject(){
        ProjectSaveRequestDto projectSaveRequestDto = ProjectSaveRequestDto.builder()
                .title("title")
                .content("content")
                .thumbnail("thumbnail")
                .state(State.PRODUCT)
                .website("website")
                .appstore("appstore")
                .playstore("playstore")
                .categories(categoryToLongList(categories))
                .teches(techToLongList(teches))
                .build();

        return projectService.create(member.getId(), projectSaveRequestDto);
    }
}