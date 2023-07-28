package network.picky.web.category.service;

import network.picky.web.category.domain.Category;
import network.picky.web.category.dto.CategoryDeleteAllRequestDto;
import network.picky.web.category.dto.CategoryResponseDto;
import network.picky.web.category.dto.CategorySaveRequestDto;
import network.picky.web.category.exception.CategoryExistsException;
import network.picky.web.category.exception.CategoryNotFoundException;
import network.picky.web.category.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class, Mockito.RETURNS_DEEP_STUBS);
    CategoryService categoryService = new CategoryService(categoryRepository);

    @Test
    @DisplayName("readAll : 정상")
    public void testReadAll() {
        //given
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1L, "category1"));
        categories.add(new Category(2L, "category2"));
        categories.add(new Category(3L, "category3"));
        categories.add(new Category(4L, "category4"));
        Mockito.when(categoryRepository.findAll()).thenReturn(categories);

        //when
        List<CategoryResponseDto> categoryDtos = categoryService.readAll();

        //then
        List<Long> returnIds = categoryDtos.stream().map(value -> value.getId()).collect(Collectors.toList());
        List<Long> categoryIds = categories.stream().map(value -> value.getId()).collect(Collectors.toList());

        assertEquals(returnIds, categoryIds);
    }

    @Test
    @DisplayName("readAll : 읽어온 값이 비어 있을 경우")
    public void testReadAllEmpty() {
        //given
        List<Category> categories = new ArrayList<>();
        Mockito.when(categoryRepository.findAll()).thenReturn(categories);

        //when
        List<CategoryResponseDto> categoryDtos = categoryService.readAll();

        //then
        assertEquals(categoryDtos, Collections.emptyList());
    }

    @Test
    @DisplayName("deleteAlByRequest : 정상")
    public void testDeleteAllByRequest() {
        //given
        List<Long> ids = LongStream.range(1, 5).boxed().toList();
        CategoryDeleteAllRequestDto categoryDeleteAllRequestDto = new CategoryDeleteAllRequestDto(ids);

        List<Category> categories = ids.stream().map(n -> new Category(n, "category" + n)).collect(Collectors.toList());
        Mockito.when(categoryRepository.findAllById(ids)).thenReturn(categories);
        Mockito.when(categoryRepository.count()).thenReturn(categories.stream().count());

        //when
        categoryService.deleteAllByRequest(categoryDeleteAllRequestDto);

        //then
        Mockito.verify(categoryRepository).deleteAll(categories);
    }

    @Test
    @DisplayName("deleteAlByRequest : ids 길이가 초과 했을떄")
    public void deleteAllByRequestTestIdsTooLong() {
        //given
        List<Long> ids = LongStream.range(1, 100000).boxed().toList();
        CategoryDeleteAllRequestDto categoryDeleteAllRequestDto = new CategoryDeleteAllRequestDto(ids);

        List<Long> realIds = LongStream.range(1, 5).boxed().toList();
        List<Category> categories = realIds.stream().map(n -> new Category(n, "category" + n)).collect(Collectors.toList());
        Mockito.when(categoryRepository.findAllById(ids)).thenReturn(categories);
        Mockito.when(categoryRepository.count()).thenReturn(categories.stream().count());

        //when
        Executable excute = () -> categoryService.deleteAllByRequest(categoryDeleteAllRequestDto);

        //then
        assertThrows(CategoryNotFoundException.class, excute);
    }

    @Test
    @DisplayName("deleteAlByRequest : 존재하지 않는 카테고리를 삭제요청 할때")
    public void deleteAllByRequestTestCategoryNotExists() {
        //given
        List<Long> ids = LongStream.range(2, 6).boxed().toList();
        CategoryDeleteAllRequestDto categoryDeleteAllRequestDto = new CategoryDeleteAllRequestDto(ids);

        List<Long> realIds = LongStream.range(1, 5).boxed().toList();
        List<Category> categories = realIds.stream().map(n -> new Category(n, "category" + n)).collect(Collectors.toList());
        Mockito.when(categoryRepository.findAllById(ids)).thenReturn(categories);
        Mockito.when(categoryRepository.count()).thenReturn(categories.stream().count());

        //when
        Executable excute = () -> categoryService.deleteAllByRequest(categoryDeleteAllRequestDto);

        //then
        assertThrows(CategoryNotFoundException.class, excute);
    }


    @Test
    @DisplayName("create : 정상")
    public void create() {
        //given
        final String categoryName = "category";
        CategorySaveRequestDto saveRequestDto = new CategorySaveRequestDto(categoryName);

        Mockito.when(categoryRepository.findByName(saveRequestDto.getName()).isPresent()).thenReturn(false);

        Long createdCategoryId = 1L;
        Category category = new Category(createdCategoryId, categoryName);
        Mockito.when(categoryRepository.save(Mockito.any(Category.class))).thenReturn(category);

        //when
        CategoryResponseDto categoryResponseDto = categoryService.create(saveRequestDto);

        //then
        assertEquals(createdCategoryId, categoryResponseDto.getId());
        assertEquals(categoryName, categoryResponseDto.getName());
    }

    @Test
    @DisplayName("create : 이미 존재하는 카테고리인 경우")
    public void createTestCategoryAlreadyExists() {
        //given
        final String categoryName = "category";
        CategorySaveRequestDto saveRequestDto = new CategorySaveRequestDto(categoryName);

        Mockito.when(categoryRepository.findByName(saveRequestDto.getName()).isPresent()).thenReturn(true);

        //when
        Executable executable = () -> categoryService.create(saveRequestDto);

        //then
        assertThrows(CategoryExistsException.class, executable);
    }

    @Test
    @DisplayName("update : 정상")
    public void update() {
        //given
        final Long id = 1L;
        final String categoryName = "category";

        Category category = new Category(id, categoryName);
        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(category));
        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        final String newCategoryName = "new_category";
        CategorySaveRequestDto saveRequestDto = new CategorySaveRequestDto(newCategoryName);

        //when
        CategoryResponseDto categoryResponseDto = categoryService.update(id, saveRequestDto);

        //then
        assertEquals(category.getName(), categoryResponseDto.getName());
    }

    @Test
    @DisplayName("update : 카테고리를 찾을 수 없음")
    public void updateTestCategoryNotFound() {
        //given
        final Long id = 1L;
        final String newCategoryName = "new_category";
        CategorySaveRequestDto saveRequestDto = new CategorySaveRequestDto(newCategoryName);

        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Executable executable = () -> categoryService.update(id, saveRequestDto);

        //then
        assertThrows(CategoryNotFoundException.class, executable);
    }

    @Test
    @DisplayName("delete : 정상")
    public void delete() {
        //given
        final Long id = 1L;
        final String categoryName = "category";
        Category category = new Category(id, categoryName);
        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.of(category));

        //when
        categoryService.delete(id);

        //then
        Mockito.verify(categoryRepository).delete(category);

    }

    @Test
    @DisplayName("delete : 카테고리를 찾을 수 없음")
    public void deleteTestCategoryNotFound() {
        //given
        final Long id = 1L;
        Mockito.when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Executable executable = () -> categoryService.delete(id);

        //then
        assertThrows(CategoryNotFoundException.class, executable);

    }


}