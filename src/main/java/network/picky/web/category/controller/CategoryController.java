package network.picky.web.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.category.dto.CategoryDeleteAllRequestDto;
import network.picky.web.category.dto.CategoryResponseDto;
import network.picky.web.category.dto.CategorySaveRequestDto;
import network.picky.web.category.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping
@RestController
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/category")
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        List<CategoryResponseDto> categoryResponseDtos = categoryService.readAll();
        return ResponseEntity.ok(categoryResponseDtos);
    }

    @DeleteMapping("/admin/category")
    public ResponseEntity deleteAllByRequest(@RequestBody @Valid CategoryDeleteAllRequestDto categoryDeleteAllRequestDto) {
        categoryService.deleteAllByRequest(categoryDeleteAllRequestDto);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/admin/category")
    public ResponseEntity post(@RequestBody @Valid CategorySaveRequestDto categorySaveRequestDto) {
        categoryService.create(categorySaveRequestDto);
        URI readAllLocation = ServletUriComponentsBuilder.fromCurrentContextPath().path("/category").build().toUri();
        return ResponseEntity.created(readAllLocation).build();
    }

    @PutMapping("/admin/category/{id}")
    public ResponseEntity<CategoryResponseDto> update(@PathVariable Long id, @RequestBody @Valid CategorySaveRequestDto categorySaveRequestDto) {
        CategoryResponseDto categoryResponseDto = categoryService.update(id, categorySaveRequestDto);
        return ResponseEntity.ok(categoryResponseDto);
    }

    @DeleteMapping("/admin/category/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
