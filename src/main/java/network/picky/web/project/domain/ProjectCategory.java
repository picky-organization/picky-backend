package network.picky.web.project.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.category.domain.Category;

@Getter
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "project_id", "category_id" }) })
@Entity
public class ProjectCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    public ProjectCategory(Project project, Category category) {
        this.project = project;
        this.category = category;
    }
}
