package network.picky.web.project.repository;

import network.picky.web.project.domain.ProjectTech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectTechRepository extends JpaRepository<ProjectTech, Long> {
}
