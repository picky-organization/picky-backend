package network.picky.web.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.category.domain.Category;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategorySaveRequestDto {
    @NotBlank
    @Size(max = 20)
    private String name;

    public Category toEntity() {
        return Category.builder().name(this.name).build();
    }
}
