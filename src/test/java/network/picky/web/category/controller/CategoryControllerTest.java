package network.picky.web.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.category.dto.CategoryDeleteAllRequestDto;
import network.picky.web.category.dto.CategoryResponseDto;
import network.picky.web.category.dto.CategorySaveRequestDto;
import network.picky.web.category.service.CategoryService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@Import(CategoryController.class)
@WebMvcTest(controllers = CategoryController.class, useDefaultFilters = false)
class CategoryControllerTest {
//    Logger log = LoggerFactory.getLogger(CategoryControllerTest.class);

    @Autowired
    MockMvc mvc;

    @MockBean
    CategoryService categoryService;

    ObjectMapper objectMapper = new ObjectMapper();

    @TestConfiguration
    static class DefaultConfigWithoutCsrf{

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(csrf->csrf.disable());
            return http.build();
        }
    }

    @WithMockUser
    @DisplayName("get-all : 정상")
    @Test
    void getAll() throws Exception {
        String path = "/category";
        Stream<Long> ids = LongStream.range(1, 5).boxed();
        List<CategoryResponseDto> categories = ids.map(n -> new CategoryResponseDto(n, "category" + n)).collect(Collectors.toList());
        Mockito.when(categoryService.readAll()).thenReturn(categories);

        //when
        ResultActions ra = mvc.perform(get(path));

        String result = objectMapper.writeValueAsString(categories);
        System.out.println(result);
        //then
        ra.andExpect(status().isOk())
                .andExpect(content().json(result));
//                .andExpect(jsonPath("$", Matchers.hasSize(4)))
//                .andExpect(jsonPath("$[0].id").value(categories.get(0).getId()))
//                .andExpect(jsonPath("$[0].name").value(categories.get(0).getName()));
    }
    @WithMockUser
    @DisplayName("get-all : 결과가 비어 있을때")
    @Test
    void getAllTestResultEmpty() throws Exception {
        String path = "/category";
        Mockito.when(categoryService.readAll()).thenReturn(new ArrayList<>());

        //when
        ResultActions ra = mvc.perform(get(path));

        //then
        ra.andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(0)));
    }

    @WithMockUser
    @DisplayName("deleteAllByRequest : 정상")
    @Test
    void deleteAllByRequest() throws Exception {
        // given
        String path = "/admin/category";
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        CategoryDeleteAllRequestDto categoryDeleteAllRequestDto = new CategoryDeleteAllRequestDto(ids);
        String content = objectMapper.writeValueAsString(categoryDeleteAllRequestDto);

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
        String path = "/admin/category";
        CategoryDeleteAllRequestDto categoryDeleteAllRequestDto = new CategoryDeleteAllRequestDto(Collections.emptyList());
        String content = objectMapper.writeValueAsString(categoryDeleteAllRequestDto);

        //when
        ResultActions ra = mvc.perform(delete(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        //then
        ra.andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("deleteAllByRequest : request가 없을때")
    @Test
    void deleteAllByRequestTestRequestIsNull() throws Exception {
        // given
        String path = "/admin/category";
        CategorySaveRequestDto categorySaveRequestDto = new CategorySaveRequestDto("hi");
        String content = objectMapper.writeValueAsString(categorySaveRequestDto);

        //when
        ResultActions ra = mvc.perform(delete(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        //then
        ra.andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("deleteAllByRequest : request 형식이 맞지않을때")
    @Test
    void deleteAllByRequestTestRequestFrom() throws Exception {
        // given
        String path = "/admin/category";

        //when
        ResultActions ra = mvc.perform(delete(path));

        //then
        ra.andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("post : 정상")
    @Test
    void postTest() throws Exception {
        String path = "/admin/category";
        final String categoryName = "category";
        CategorySaveRequestDto categorySaveRequestDto = new CategorySaveRequestDto(categoryName);

        //when
        ResultActions ra = mvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categorySaveRequestDto)));

        //then
        ra.andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION));
    }

    @WithMockUser
    @DisplayName("post : request가 없을때")
    @Test
    void postTestRequestIsNull() throws Exception {
        String path = "/admin/category";

        //when
        ResultActions ra = mvc.perform(post(path));

        //then
        ra.andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("post : request 형식이 맞지 않을 때")
    @Test
    void postTestRequestForm() throws Exception {
        String path = "/admin/category";
        Map<String,String> request = new HashMap<>();
        request.put("test", "map");

        //when
        ResultActions ra = mvc.perform(post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        ra.andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("update : 정상")
    @Test
    void updateTest() throws Exception {
        Long id = 1L;
        String path = "/admin/category/"+id;
        final String newCategoryName = "new_category";
        CategorySaveRequestDto categorySaveRequestDto = new CategorySaveRequestDto(newCategoryName);

        CategoryResponseDto categoryResponseDto = new CategoryResponseDto(id, newCategoryName);
        Mockito.when(categoryService.update(Mockito.anyLong(), Mockito.any(CategorySaveRequestDto.class))).thenReturn(categoryResponseDto);

        //when
        ResultActions ra = mvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categorySaveRequestDto)));

        //then
        String content = objectMapper.writeValueAsString(categoryResponseDto);
        ra.andExpect(status().isOk())
                .andExpect(content().json(content));
    }

    @WithMockUser
    @DisplayName("update : 잘못된 id 일때")
    @Test
    void updateTestIdNotFound() throws Exception {
        String id = "wrongId";
        String path = "/admin/category/"+id;
        final String newCategoryName = "new_category";
        CategorySaveRequestDto categorySaveRequestDto = new CategorySaveRequestDto(newCategoryName);

        //when
        ResultActions ra = mvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categorySaveRequestDto)));

        //then
        ra.andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("update : request가 없을때")
    @Test
    void updateTestRequestNotExists() throws Exception {
        String id = "wrongId";
        String path = "/admin/category/"+id;

        //when
        ResultActions ra = mvc.perform(put(path));

        //then
        ra.andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("update : request가 안 맞을 때")
    @Test
    void updateTestRequestWrong() throws Exception {
        Long id = 1L;
        String path = "/admin/category/" + id;
        Map<String,String> request = new HashMap<>();
        request.put("test", "map");

        //when
        ResultActions ra = mvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        //then
        ra.andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("delete : 정상")
    @Test
    void deleteTest() throws Exception {
        Long id = 1L;
        String path = "/admin/category/"+id;

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
        String path = "/admin/category/"+id;

        //when
        ResultActions ra = mvc.perform(delete(path));

        //then
        ra.andExpect(status().isBadRequest());
    }

    @WithMockUser
    @DisplayName("delete : 초과한 id 일떄")
    @Test
    void deleteTestIdTooLong() throws Exception {
        BigInteger bigInteger = new BigInteger("19223372036854775807");
        String path = "/admin/category/"+bigInteger;

        //when
        ResultActions ra = mvc.perform(delete(path));

        //then
        ra.andExpect(status().isBadRequest());
    }

}