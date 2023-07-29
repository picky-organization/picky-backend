package network.picky.web.category.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.category.dto.CategoryResponseDto;
import network.picky.web.common.domain.BaseEntity;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    @Size(max = 20)
    private String name;

    public CategoryResponseDto toResponseDto() {
        return new CategoryResponseDto(this.id, this.name);
    }

    public void updateName(String name) {
        this.name = name;
    }
}
