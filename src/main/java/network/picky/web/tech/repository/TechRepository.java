package network.picky.web.tech.repository;

import network.picky.web.tech.domain.Tech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TechRepository extends JpaRepository<Tech, Long> {
    Optional<Tech> findByName(String name);
}
