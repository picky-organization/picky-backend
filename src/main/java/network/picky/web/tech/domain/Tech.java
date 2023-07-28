package network.picky.web.tech.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import network.picky.web.tech.dto.TechResponseDto;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Tech {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull
    @Size(max = 20)
    private String name;

    public TechResponseDto toResponseDto() {
        return new TechResponseDto(this.id, this.name);
    }

    public void updateName(String name) {
        this.name = name;
    }
}
