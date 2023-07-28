package network.picky.web.category.repository;

import jakarta.transaction.Transactional;
import network.picky.web.category.domain.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Transactional
class CategoryRepositoryTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("findName test")
    void testFindByName() {
        //given
        final String CATEGORY = "category_name";
        Category category = Category.builder().name(CATEGORY).build();
        categoryRepository.save(category);

        //when
        Category findCategory = categoryRepository.findByName(CATEGORY).orElseThrow();

        //then
        assertEquals(findCategory.getName(), category.getName());
    }

    @Test
    @DisplayName("findName 카테고리가 없을떄")
    void testFindByNameIfCategoryNotExist() {
        //given
        final String CATEGORY = "category_name";

        //when
        Optional<Category> findCategory = categoryRepository.findByName(CATEGORY);

        //then
        assertThrows(NoSuchElementException.class, () -> findCategory.orElseThrow());
    }

}