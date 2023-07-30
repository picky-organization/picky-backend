package network.picky.web.project.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.tech.domain.Tech;

@Getter
@EqualsAndHashCode(exclude = "id")
@NoArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "project_id", "tech_id" }) })
@Entity
public class ProjectTech {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tech_id")
    private Tech tech;

    public ProjectTech(Project project, Tech tech) {
        this.project = project;
        this.tech = tech;
    }
}
