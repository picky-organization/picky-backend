package network.picky.web.tech.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.project.domain.ProjectTech;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TechResponseDto {
    @Positive
    @Min(1)
    Long id;

    @NotBlank
    @Size(max = 20)
    private String name;

    public TechResponseDto(ProjectTech projectTech) {
        this.id = projectTech.getTech().getId();
        this.name = projectTech.getTech().getName();
    }
}
