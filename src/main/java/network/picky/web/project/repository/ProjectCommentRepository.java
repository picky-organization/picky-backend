package network.picky.web.project.repository;

import network.picky.web.project.domain.Project;
import network.picky.web.project.domain.ProjectComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectCommentRepository extends JpaRepository<ProjectComment, Long> {
    List<ProjectComment> findAllByProject(Project project);
    List<ProjectComment> findAllByProjectAndParentIsNull(Project project);
    List<ProjectComment> findAllByParent(ProjectComment projectComment);

    void deleteAllByParent(ProjectComment projectComment);

    int countByParent(ProjectComment projectComment);
}
