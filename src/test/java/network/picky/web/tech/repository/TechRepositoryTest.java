package network.picky.web.tech.repository;

import jakarta.transaction.Transactional;
import network.picky.web.tech.domain.Tech;
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
class TechRepositoryTest {
    @Autowired
    TechRepository techRepository;

    @Test
    @DisplayName("findName test")
    void testFindByName() {
        //given
        final String CATEGORY = "tech_name";
        Tech tech = Tech.builder().name(CATEGORY).build();
        techRepository.save(tech);

        //when
        Tech findTech = techRepository.findByName(CATEGORY).orElseThrow();

        //then
        assertEquals(findTech.getName(), tech.getName());
    }

    @Test
    @DisplayName("findName 기술이 없을떄")
    void testFindByNameIfTechNotExist() {
        //given
        final String CATEGORY = "tech_name";

        //when
        Optional<Tech> findTech = techRepository.findByName(CATEGORY);

        //then
        assertThrows(NoSuchElementException.class, () -> findTech.orElseThrow());
    }

}