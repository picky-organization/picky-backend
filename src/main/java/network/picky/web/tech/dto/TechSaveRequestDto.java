package network.picky.web.tech.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.tech.domain.Tech;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TechSaveRequestDto {
    @NotBlank
    @Size(max = 20)
    private String name;

    public Tech toEntity() {
        return Tech.builder().name(this.name).build();
    }
}
