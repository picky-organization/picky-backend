package network.picky.web.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.project.dto.ProjectAllResponseDto;
import network.picky.web.project.dto.ProjectResponseDto;
import network.picky.web.project.dto.ProjectSaveRequestDto;
import network.picky.web.project.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RequestMapping
@RestController
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/project")
    public ResponseEntity<Page<ProjectAllResponseDto>> getAll(Pageable pageable) {
        Page<ProjectAllResponseDto> projectAllResponseDtoPage = projectService.readAll(pageable);
        return ResponseEntity.ok(projectAllResponseDtoPage);
    }

    @PostMapping("/project")
    public ResponseEntity post(@RequestBody @Valid ProjectSaveRequestDto projectSaveRequestDto) {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProjectResponseDto projectDto = projectService.create(memberId, projectSaveRequestDto);

        URI readLocation = ServletUriComponentsBuilder.fromCurrentContextPath().path("/project/{id}").buildAndExpand(projectDto.getId()).toUri();
        return ResponseEntity.created(readLocation).build();
    }

    @GetMapping("/project/{id}")
    public ResponseEntity get(@PathVariable Long id) {
        ProjectResponseDto projectDto = projectService.read(id);
        return ResponseEntity.ok(projectDto);
    }

    @PutMapping("/project/{id}")
    public ResponseEntity<ProjectResponseDto> put(@PathVariable Long id, @RequestBody @Valid ProjectSaveRequestDto projectSaveRequestDto) {
        ProjectResponseDto projectResponseDto = projectService.update(id, projectSaveRequestDto);
        return ResponseEntity.ok(projectResponseDto);
    }

    @DeleteMapping("/project/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
