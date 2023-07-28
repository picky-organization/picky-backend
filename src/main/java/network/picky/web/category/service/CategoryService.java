package network.picky.web.category.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import network.picky.web.category.domain.Category;
import network.picky.web.category.dto.CategoryDeleteAllRequestDto;
import network.picky.web.category.dto.CategoryResponseDto;
import network.picky.web.category.dto.CategorySaveRequestDto;
import network.picky.web.category.exception.CategoryExistsException;
import network.picky.web.category.exception.CategoryNotFoundException;
import network.picky.web.category.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDto> readAll() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(category -> category.toResponseDto()).collect(Collectors.toList());
    }

    public void deleteAllByRequest(CategoryDeleteAllRequestDto categoryDeleteAllRequestDto) throws CategoryNotFoundException {
        if(categoryDeleteAllRequestDto.getIds().size() > categoryRepository.count()){
            throw new CategoryNotFoundException();
        }

        List<Category> categories = categoryRepository.findAllById(categoryDeleteAllRequestDto.getIds());
        if (requestCategoriesExistsAll(categoryDeleteAllRequestDto, categories)) {
            categoryRepository.deleteAll(categories);
        }
        else{
            throw new CategoryNotFoundException();
        }
    }

    public CategoryResponseDto create(CategorySaveRequestDto categorySaveRequestDto) throws CategoryExistsException {
        if (categoryRepository.findByName(categorySaveRequestDto.getName()).isPresent()) {
            throw new CategoryExistsException();
        }
        Category category = categorySaveRequestDto.toEntity();
        category = categoryRepository.save(category);
        return category.toResponseDto();
    }

    public CategoryResponseDto update(Long id, CategorySaveRequestDto categorySaveRequestDto) throws CategoryNotFoundException {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        category.updateName(categorySaveRequestDto.getName());
        category = categoryRepository.save(category);
        return category.toResponseDto();
    }

    public void delete(Long id) throws CategoryNotFoundException {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
        categoryRepository.delete(category);
    }

    private boolean requestCategoriesExistsAll(CategoryDeleteAllRequestDto categoryDeleteAllRequestDto, List<Category> findCategories) {
        List<Long> findCategoryIds = findCategories.stream().map(category -> category.getId()).collect(Collectors.toList());
        List<Long> requestCategoryIds = categoryDeleteAllRequestDto.getIds();
        return findCategoryIds.containsAll(requestCategoryIds);
    }
}