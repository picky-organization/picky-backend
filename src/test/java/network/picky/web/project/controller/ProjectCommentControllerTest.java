package network.picky.web.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import network.picky.web.category.domain.Category;
import network.picky.web.common.error.GlobalExceptionHandler;
import network.picky.web.config.TestConfig;
import network.picky.web.member.domain.Member;
import network.picky.web.member.enums.Role;
import network.picky.web.project.domain.Project;
import network.picky.web.project.domain.ProjectCategory;
import network.picky.web.project.domain.ProjectComment;
import network.picky.web.project.domain.ProjectTech;
import network.picky.web.project.dto.ProjectCommentCreateRequestDto;
import network.picky.web.project.dto.ProjectCommentResponseDto;
import network.picky.web.project.dto.ProjectCommentUpdateRequestDto;
import network.picky.web.project.enums.State;
import network.picky.web.project.exception.ProjectCommentNotFoundException;
import network.picky.web.project.exception.ProjectNotFoundException;
import network.picky.web.project.service.ProjectCommentService;
import network.picky.web.tech.domain.Tech;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({ProjectCommentController.class, TestConfig.class, GlobalExceptionHandler.class})
@WebMvcTest(useDefaultFilters = false)
class ProjectCommentControllerTest {
    @Autowired
    MockMvc mvc;

    @MockBean
    ProjectCommentService projectCommentService;
    ObjectMapper objectMapper = new ObjectMapper();

    Long memberId = 1L;
    Long projectId = 2L;
    Long commentId = 3L;
    @WithMockUser
    @DisplayName("getAll : 정상")
    @Test
    void getAllTest() throws Exception {
        //given
        String path = "/project/"+projectId+"/comment";

        Member member = createMember(memberId);
        Project project = createProject(projectId, member);
        ProjectComment projectCOmment = createProjectComment(member, project);
        ProjectCommentResponseDto projectCommentResponseDto = new ProjectCommentResponseDto(projectCOmment);
        List<ProjectCommentResponseDto> list = List.of(projectCommentResponseDto);
        Mockito.when(projectCommentService.readAll(project.getId(), null)).thenReturn(list);

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().json(objectMapper.writeValueAsString(list)));
    }

    @WithMockUser
    @DisplayName("getAll : empty")
    @Test
    void getAllEmpty() throws Exception {
        //given
        String path = "/project/"+projectId+"/comment";

        Member member = createMember(memberId);
        Project project = createProject(projectId, member);
        Mockito.when(projectCommentService.readAll(project.getId(), null)).thenReturn(Collections.emptyList());

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().json("[]"));
    }

    @WithMockUser
    @DisplayName("getAll : project id가 잘못 된 경우")
    @Test
    void getAllWrongId() throws Exception {
        //given
        String path = "/project/wrong/comment";

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("getAll : Project Not Found")
    @Test
    void getAllProjectNotFound() throws Exception {
        //given
        String path = "/project/"+projectId+"/comment";

        Mockito.when(projectCommentService.readAll(projectId, null)).thenThrow(ProjectNotFoundException.class);

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ProjectNotFoundException))
                .andExpect(status().isNotFound());
    }

    @WithMockUser
    @DisplayName("post : 정상")
    @Test
    void postTest() throws Exception {
        //given
        String path = "/project/"+projectId+"/comment";
        try (MockedStatic<SecurityContextHolder> security = Mockito.mockStatic(SecurityContextHolder.class, Mockito.RETURNS_DEEP_STUBS)) {
            security.when(SecurityContextHolder.getContext().getAuthentication()::getPrincipal).thenReturn(memberId);

            Member member = createMember(memberId);
            Project project = createProject(projectId, member);
            ProjectComment projectCOmment = createProjectComment(member, project);
            ProjectCommentResponseDto projectCommentResponseDto = Mockito.spy(new ProjectCommentResponseDto(projectCOmment));
            Mockito.when(projectCommentResponseDto.getId()).thenReturn(commentId);
            Mockito.when(projectCommentService.create(eq(projectId), eq(memberId), any(ProjectCommentCreateRequestDto.class))).thenReturn(projectCommentResponseDto);

            ProjectCommentCreateRequestDto projectCommentCreateRequestDto = ProjectCommentCreateRequestDto.builder()
                    .content("hi")
                    .parentId(null)
                    .build();
            //when
            ResultActions ra = mvc.perform(post(path)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(projectCommentCreateRequestDto)));

            //then
            ra
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(status().isCreated())
                    .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/project/comment/" + commentId));
        }
    }

    @WithMockUser
    @DisplayName("post : empty request")
    @Test
    void postTestEmptyRequest() throws Exception {
        //given
        String path = "/project/"+projectId+"/comment";

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
        String path = "/project/"+projectId+"/comment";

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
        String path = "/project/"+projectId+"/comment/"+commentId;

        Member member = createMember(memberId);
        Project project = createProject(projectId, member);
        ProjectComment projectCOmment = createProjectComment(member, project);
        ProjectCommentResponseDto projectCommentResponseDto = new ProjectCommentResponseDto(projectCOmment);

        Mockito.when(projectCommentService.read(commentId)).thenReturn(projectCommentResponseDto);

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(projectCommentResponseDto)));

    }

    @WithMockUser
    @DisplayName("get : Not Found")
    @Test
    void getTestNotFound() throws Exception {
        //given
        String path = "/project/"+projectId+"/comment/"+commentId;

        Mockito.when(projectCommentService.read(commentId)).thenThrow(ProjectCommentNotFoundException.class);

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ProjectCommentNotFoundException))
                .andExpect(status().isNotFound());
    }
    @WithMockUser
    @DisplayName("get : 잘못된 id 일때")
    @Test
    void getTestIdWrong() throws Exception {
        //given
        String path = "/project/"+projectId+"/comment/"+"wrong";

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
        String path = "/project/"+projectId+"/comment/"+ Long.MAX_VALUE+1;

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
        String path = "/project/"+projectId+"/comment/"+commentId;

        Member member = createMember(memberId);
        Project project = createProject(projectId, member);
        ProjectComment projectComment = createProjectComment(member, project);
        ProjectCommentResponseDto projectCommentResponseDto = Mockito.spy(new ProjectCommentResponseDto(projectComment));
        Mockito.when(projectCommentResponseDto.getId()).thenReturn(commentId);
        Mockito.when(projectCommentService.update(eq(commentId), any(ProjectCommentUpdateRequestDto.class)))
                .thenReturn(projectCommentResponseDto);

        ProjectCommentUpdateRequestDto projectCommentUpdateRequestDto = ProjectCommentUpdateRequestDto.builder()
                .content("newasd")
                .build();
        //when
        ResultActions ra = mvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectCommentUpdateRequestDto)));

        //then
        ra
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(projectCommentResponseDto)));
    }
    @WithMockUser
    @DisplayName("put : empty request")
    @Test
    void putTestEmptyRequest() throws Exception {
        //given
        String path = "/project/"+projectId+"/comment/"+commentId;

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
        String path = "/project/"+projectId+"/comment/"+commentId;

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
        String path = "/project/"+projectId+"/comment/"+"wrong";

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
        String path = "/project/"+projectId+"/comment/" + Long.MAX_VALUE+1;

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
        String path = "/project/"+projectId+"/comment/" + commentId;

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
        String path = "/project/"+projectId+"/comment/" + "wrong";

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
        String path = "/project/"+projectId+"/comment/" + Long.MAX_VALUE+1;

        //when
        ResultActions ra = mvc.perform(delete(path));

        //then
        ra
                .andDo(MockMvcResultHandlers.print())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException))
                .andExpect(status().isBadRequest());
    }

    Member createMember(Long memberId) {
        Member member = Mockito.spy(Member.builder()
                .email("email")
                .name("name")
                .picture("picture")
                .socialType("google")
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

    ProjectComment createProjectComment(Member member, Project project){
        ProjectComment projectCOmment = ProjectComment.builder()
                .member(member)
                .project(project)
                .parent(null)
                .content("comment")
                .build();
        return projectCOmment;
    }
}