package network.picky.web.tech.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TechDeleteAllRequestDto {

    @Size(min = 1)
    @NotNull
    List<Long> ids;
}
