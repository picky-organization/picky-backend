package network.picky.web.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import network.picky.web.category.domain.Category;
import network.picky.web.common.error.GlobalExceptionHandler;
import network.picky.web.config.TestConfig;
import network.picky.web.member.domain.Member;
import network.picky.web.member.enums.Role;
import network.picky.web.project.domain.Project;
import network.picky.web.project.domain.ProjectCategory;
import network.picky.web.project.domain.ProjectTech;
import network.picky.web.project.dto.ProjectAllResponseDto;
import network.picky.web.project.dto.ProjectResponseDto;
import network.picky.web.project.dto.ProjectSaveRequestDto;
import network.picky.web.project.enums.State;
import network.picky.web.project.exception.ProjectNotFoundException;
import network.picky.web.project.service.ProjectService;
import network.picky.web.tech.domain.Tech;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({ProjectController.class, TestConfig.class, GlobalExceptionHandler.class})
@WebMvcTest(useDefaultFilters = false)
class ProjectControllerTest {
    @Autowired
    MockMvc mvc;
    @MockBean
    ProjectService projectService;
    ObjectMapper objectMapper = new ObjectMapper();

    Long memberId = 1L;
    Long projectId = 2L;

    @WithMockUser
    @DisplayName("getAll : 정상")
    @Test
    void getAllTest() throws Exception {
        //given
        String path = "/project";

        Member member = createMember(memberId);
        Project project = createProject(projectId, member);
        ProjectAllResponseDto projectAllResponseDto = new ProjectAllResponseDto(project);
        List<ProjectAllResponseDto> list = List.of(projectAllResponseDto);
        Page<ProjectAllResponseDto> responsePage = new PageImpl<>(list);
        Mockito.when(projectService.readAll(any(Pageable.class))).thenReturn(responsePage);

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responsePage)))
                .andExpect(jsonPath("$.totalElements", Matchers.is(1)))
                .andDo(MockMvcResultHandlers.print());
    }

    @WithMockUser
    @DisplayName("getAll : empty")
    @Test
    void getAllTestResultEmpty() throws Exception {
        //given
        String path = "/project";

        Page<ProjectAllResponseDto> responsePage = new PageImpl<>(Collections.emptyList());
        Mockito.when(projectService.readAll(any(Pageable.class))).thenReturn(responsePage);

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responsePage)))
                .andExpect(jsonPath("$.totalElements", Matchers.is(0)))
                .andDo(MockMvcResultHandlers.print());
    }

    @WithMockUser
    @DisplayName("post : 정상")
    @Test
    void postTest() throws Exception {
        //given
        String path = "/project";

        ProjectSaveRequestDto projectSaveRequestDto = createProjectSaveRequestDto();

        try (MockedStatic<SecurityContextHolder> security = Mockito.mockStatic(SecurityContextHolder.class, Mockito.RETURNS_DEEP_STUBS)) {
            security.when(SecurityContextHolder.getContext().getAuthentication()::getPrincipal).thenReturn(memberId);

            ProjectResponseDto projectResponseDto = Mockito.mock();
            Mockito.when(projectResponseDto.getId()).thenReturn(projectId);
            Mockito.when(projectService.create(eq(memberId), any(ProjectSaveRequestDto.class))).thenReturn(projectResponseDto);

            //when
            ResultActions ra = mvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(projectSaveRequestDto)));

            //then
            ra
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/project/" + projectId));
        }
    }

    @WithMockUser
    @DisplayName("post : empty request")
    @Test
    void postTestEmptyRequest() throws Exception {
        //given
        String path = "/project";


        //when
        ResultActions ra = mvc.perform(post(path));
        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("post : invalid request")
    @Test
    void postTestInvalidRequest() throws Exception {
        String path = "/project";

        Map<String, String> request = new HashMap<>();

        //when
        ResultActions ra = mvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("get : 정상")
    @Test
    void getTest() throws Exception {
        String path = "/project/" + projectId;

        Member member = createMember(memberId);
        Project project = createProject(projectId, member);
        ProjectResponseDto projectResponseDto = new ProjectResponseDto(project);
        Mockito.when(projectService.read(projectId)).thenReturn(projectResponseDto);

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(projectResponseDto)));

    }

    @WithMockUser
    @DisplayName("get : Not Found")
    @Test
    void getTestNotFound() throws Exception {
        //given
        String path = "/project/" + projectId;

        Mockito.when(projectService.read(projectId)).thenThrow(ProjectNotFoundException.class);

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ProjectNotFoundException))
                .andExpect(status().isNotFound());
    }
    @WithMockUser
    @DisplayName("get : 잘못된 id 일때")
    @Test
    void getTestIdWrong() throws Exception {
        //given
        String path = "/project/" + "wrong";

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("get : 초과한 id 일때")
    @Test
    void getTestIdToLong() throws Exception {
        //given
        String path = "/project/" + Long.MAX_VALUE+1;

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("put : 정상")
    @Test
    void putTest() throws Exception {
        //given
        String path = "/project/" + projectId;

        ProjectSaveRequestDto projectSaveRequestDto = createProjectSaveRequestDto();

        Member member = createMember(memberId);
        Project project = createProject(projectId, member);
        ProjectResponseDto projectResponseDto = new ProjectResponseDto(project);
        Mockito.when(projectService.update(eq(projectId), any(ProjectSaveRequestDto.class)))
                .thenReturn(projectResponseDto);

        //when
        ResultActions ra = mvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectSaveRequestDto)));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(projectResponseDto)));
    }
    @WithMockUser
    @DisplayName("put : empty request")
    @Test
    void putTestEmptyRequest() throws Exception {
        //given
        String path = "/project/" + projectId;


        //when
        ResultActions ra = mvc.perform(put(path));
        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("put : invalid request")
    @Test
    void putTestInvalidRequest() throws Exception {
        //given
        String path = "/project/" + projectId;

        Map<String, String> request = new HashMap<>();

        //when
        ResultActions ra = mvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("put : 잘못된 id 일때")
    @Test
    void putTestIdWrong() throws Exception {
        //given
        String path = "/project/" + "wrong";

        //when
        ResultActions ra = mvc.perform(put(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("put : 초과한 id 일때")
    @Test
    void putTestIdToLong() throws Exception {
        //given
        String path = "/project/" + Long.MAX_VALUE+1;

        //when
        ResultActions ra = mvc.perform(put(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("delete : 정상")
    @Test
    void deleteTest() throws Exception {
        //given
        String path = "/project/" + projectId;

        //when
        ResultActions ra = mvc.perform(delete(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser
    @DisplayName("delete : 잘못된 id 일때")
    @Test
    void deleteTestIdWrong() throws Exception {
        //given
        String path = "/project/" + "wrong";

        //when
        ResultActions ra = mvc.perform(delete(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("delete : 초과한 id 일때")
    @Test
    void deleteTestIdToLonng() throws Exception {
        //given
        String path = "/project/" + Long.MAX_VALUE+1;

        //when
        ResultActions ra = mvc.perform(delete(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(status().isBadRequest());
    }

    ProjectSaveRequestDto createProjectSaveRequestDto() {
        return ProjectSaveRequestDto.builder()
                .title("title")
                .content("content")
                .thumbnail("https://thumbnail.com")
                .state(State.PRODUCT)
                .website("https://website.com")
                .appstore("https://appstore.com")
                .playstore("https://playstore")
                .categories(LongStream.range(1, 5).boxed().toList())
                .teches(LongStream.range(1, 5).boxed().toList())
                .build();
    }

    Member createMember(Long memberId) {
        Member member = Mockito.spy(Member.builder()
                .email("email")
                .name("name")
                .picture("picture")
                .oauth2Id("google")
                .role(Role.USER)
                .build());
        Mockito.when(member.getId()).thenReturn(memberId);

        return member;
    }


    Project createProject(Long projectId, Member member) {
        Project project = Mockito.spy(Project.builder()
                .title("title")
                .content("content")
                .thumbnail("https://thumbnail.com")
                .state(State.PRODUCT)
                .website("https://website.com")
                .appstore("https://appstore.com")
                .playstore("https://playstore")
                .member(member)
                .build());
        project.updateProjectCategories(List.of(new ProjectCategory(project, new Category(1L, "category"))));
        project.updateProjectTeches(List.of(new ProjectTech(project, new Tech(1L, "tech"))));
        Mockito.when(project.getId()).thenReturn(projectId);

        return project;
    }

}