package network.picky.web.project.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.project.dto.ProjectCommentCreateRequestDto;
import network.picky.web.project.dto.ProjectCommentResponseDto;
import network.picky.web.project.dto.ProjectCommentUpdateRequestDto;
import network.picky.web.project.service.ProjectCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping
@RestController
public class ProjectCommentController {
    private final ProjectCommentService projectCommentService;

    @GetMapping("/project/{projectId}/comment")
    public ResponseEntity<List<ProjectCommentResponseDto>> getAll(@PathVariable Long projectId, @RequestParam(required = false) Long parentId) {
        List<ProjectCommentResponseDto> projectCommentResponseDtos = projectCommentService.readAll(projectId, parentId);
        return ResponseEntity.ok(projectCommentResponseDtos);
    }

    @PostMapping("/project/{projectId}/comment")
    public ResponseEntity post(@PathVariable Long projectId, @RequestBody @Valid ProjectCommentCreateRequestDto projectCommentCreateRequestDto) {
        Long memberId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProjectCommentResponseDto projectCommentResponseDto = projectCommentService.create(projectId, memberId, projectCommentCreateRequestDto);
        URI readLocation = ServletUriComponentsBuilder.fromCurrentContextPath().path("/project/comment/{commentId}").buildAndExpand(projectCommentResponseDto.getId()).toUri();
        return ResponseEntity.created(readLocation).build();
    }

    @GetMapping("/project/{projectId}/comment/{commentId}")
    public ResponseEntity<ProjectCommentResponseDto> get(@PathVariable("commentId") Long commentId) {
        ProjectCommentResponseDto projectCommentResponseDto = projectCommentService.read(commentId);
        return ResponseEntity.ok(projectCommentResponseDto);
    }

    @PutMapping("/project/{projectId}/comment/{commentId}")
    public ResponseEntity<ProjectCommentResponseDto> put(@PathVariable("commentId") Long commentId, @RequestBody @Valid ProjectCommentUpdateRequestDto projectCommentUpdateRequestDto) {
        ProjectCommentResponseDto projectCommentResponseDto = projectCommentService.update(commentId, projectCommentUpdateRequestDto);
        return ResponseEntity.ok(projectCommentResponseDto);
    }

    @DeleteMapping("/project/{projectId}/comment/{commentId}")
    public ResponseEntity delete(@PathVariable("commentId") Long commentId) {
        projectCommentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }

}
