package network.picky.web.category.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.category.domain.Category;
import network.picky.web.project.domain.ProjectCategory;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {
    @Positive
    @Min(1)
    Long id;

    @NotBlank
    @Size(max = 20)
    private String name;

    public CategoryResponseDto(Category category){
        this.id = category.getId();
        this.name = category.getName();
    }

    public CategoryResponseDto(ProjectCategory projectCategory) {
        this.id = projectCategory.getCategory().getId();
        this.name = projectCategory.getCategory().getName();
    }
}
