package network.picky.web.tech.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.config.TestConfig;
import network.picky.web.tech.dto.TechDeleteAllRequestDto;
import network.picky.web.tech.dto.TechResponseDto;
import network.picky.web.tech.dto.TechSaveRequestDto;
import network.picky.web.tech.service.TechService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Import({TechController.class, TestConfig.class})
@WebMvcTest(useDefaultFilters = false)
class TechControllerTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    TechService techService;
    ObjectMapper objectMapper = new ObjectMapper();

    @WithMockUser
    @DisplayName("getAll : 정상")
    @Test
    void getAllTest() throws Exception {
        String path = "/tech";
        Stream<Long> ids = LongStream.range(1, 5).boxed();
        List<TechResponseDto> teches = ids.map(n -> new TechResponseDto(n, "tech" + n)).collect(Collectors.toList());
        Mockito.when(techService.readAll()).thenReturn(teches);

        //when
        ResultActions ra = mvc.perform(get(path));

        String result = objectMapper.writeValueAsString(teches);
        System.out.println(result);
        //then
        ra.andExpect(status().isOk())
                .andExpect(content().json(result));
    }

    @WithMockUser
    @DisplayName("getAll : empty")
    @Test
    void getAllTestResultEmpty() throws Exception {
        String path = "/tech";
        Mockito.when(techService.readAll()).thenReturn(new ArrayList<>());

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra.andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @WithMockUser
    @DisplayName("deleteAllByRequest : 정상")
    @Test
    void deleteAllByRequestTest() throws Exception {
        // given
        String path = "/admin/tech";
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        TechDeleteAllRequestDto techDeleteAllRequestDto = new TechDeleteAllRequestDto(ids);
        String content = objectMapper.writeValueAsString(techDeleteAllRequestDto);

        //when
        ResultActions ra = mvc.perform(delete(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        //then
        ra.andExpect(status().isNoContent());
    }

    @WithMockUser
    @DisplayName("deleteAllByRequest : ids가 빈 값일때")
    @Test
    void deleteAllByRequestTestRequestIdsIsZero() throws Exception {
        // given
        String path = "/admin/tech";
        TechDeleteAllRequestDto techDeleteAllRequestDto = new TechDeleteAllRequestDto(Collections.emptyList());
        String content = objectMapper.writeValueAsString(techDeleteAllRequestDto);

        //when
        ResultActions ra = mvc.perform(delete(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        //then
        ra.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

    }

    @WithMockUser
    @DisplayName("deleteAllByRequest : empty request")
    @Test
    void deleteAllByRequestTestEmptyRequest() throws Exception {
        // given
        String path = "/admin/tech";

        //when
        ResultActions ra = mvc.perform(delete(path));

        //then
        ra.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException));
    }

    @WithMockUser
    @DisplayName("deleteAllByRequest : invalid request")
    @Test
    void deleteAllByRequestTestRequestFrom() throws Exception {
        // given
        String path = "/admin/tech";

        Map<String, String> request = new HashMap<>();


        //when
        ResultActions ra = mvc.perform(delete(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        ra.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

    }

    @WithMockUser
    @DisplayName("post : 정상")
    @Test
    void postTest() throws Exception {
        String path = "/admin/tech";
        final String techName = "tech";
        TechSaveRequestDto techSaveRequestDto = new TechSaveRequestDto(techName);

        //when
        ResultActions ra = mvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(techSaveRequestDto)));

        //then
        ra.andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @WithMockUser
    @DisplayName("post : empty request")
    @Test
    void postTestEmptyRequest() throws Exception {
        String path = "/admin/tech";

        //when
        ResultActions ra = mvc.perform(post(path));

        //then
        ra.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException));

    }

    @WithMockUser
    @DisplayName("post : invalid request ")
    @Test
    void postTestInvalidRequest() throws Exception {
        String path = "/admin/tech";

        Map<String,String> request = new HashMap<>();

        //when
        ResultActions ra = mvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        ra.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

    }

    @WithMockUser
    @DisplayName("put : 정상")
    @Test
    void putTest() throws Exception {
        Long id = 1L;
        String path = "/admin/tech/"+id;
        final String newTechName = "new_tech";
        TechSaveRequestDto techSaveRequestDto = new TechSaveRequestDto(newTechName);

        TechResponseDto techResponseDto = new TechResponseDto(id, newTechName);
        Mockito.when(techService.update(Mockito.anyLong(), Mockito.any(TechSaveRequestDto.class))).thenReturn(techResponseDto);

        //when
        ResultActions ra = mvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(techSaveRequestDto)));

        //then
        String content = objectMapper.writeValueAsString(techResponseDto);
        ra.andExpect(status().isOk())
                .andExpect(content().json(content));
    }

    @WithMockUser
    @DisplayName("put : empty request")
    @Test
    void putTestEmptyRequest() throws Exception {
        Long id = 1L;
        String path = "/admin/tech/"+id;

        //when
        ResultActions ra = mvc.perform(put(path));

        //then
        ra.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException));

    }

    @WithMockUser
    @DisplayName("put : invalid request")
    @Test
    void putTestInvalidRequest() throws Exception {
        Long id = 1L;
        String path = "/admin/tech/" + id;

        Map<String,String> request = new HashMap<>();

        //when
        ResultActions ra = mvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        ra.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException));

    }

    @WithMockUser
    @DisplayName("put : 잘못된 id 일때")
    @Test
    void putTestWrongId() throws Exception {
        String id = "wrongId";
        String path = "/admin/tech/"+id;
        final String newTechName = "new_tech";
        TechSaveRequestDto techSaveRequestDto = new TechSaveRequestDto(newTechName);

        //when
        ResultActions ra = mvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(techSaveRequestDto)));

        //then
        ra.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException));

    }

    @WithMockUser
    @DisplayName("put : 초과한 id 일때")
    @Test
    void putTestIdTooLong() throws Exception {
        //given
        String path = "/admin/tech/" + Long.MAX_VALUE + 1;

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
        Long id = 1L;
        String path = "/admin/tech/"+id;

        //when
        ResultActions ra = mvc.perform(delete(path));

        //then
        ra.andExpect(status().isNoContent());
    }

    @WithMockUser
    @DisplayName("delete : 잘못된 id 일때")
    @Test
    void deleteTestIdWrong() throws Exception {
        String id = "1L";
        String path = "/admin/tech/"+id;

        //when
        ResultActions ra = mvc.perform(delete(path));

        //then
        ra.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException));

    }

    @WithMockUser
    @DisplayName("delete : 초과한 id 일떄")
    @Test
    void deleteTestIdTooLong() throws Exception {
        BigInteger bigInteger = new BigInteger("19223372036854775807");
        String path = "/admin/tech/"+bigInteger;

        //when
        ResultActions ra = mvc.perform(delete(path));

        //then
        ra.andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentTypeMismatchException));
    }

}