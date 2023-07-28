package network.picky.web.tech.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import network.picky.web.tech.dto.TechDeleteAllRequestDto;
import network.picky.web.tech.dto.TechResponseDto;
import network.picky.web.tech.dto.TechSaveRequestDto;
import network.picky.web.tech.service.TechService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping
@RestController
public class TechController {
    private final TechService techService;

    @GetMapping("/tech")
    public ResponseEntity<List<TechResponseDto>> getAll() {
        List<TechResponseDto> techResponseDtos = techService.readAll();
        return ResponseEntity.ok(techResponseDtos);
    }

    @DeleteMapping("/admin/tech")
    public ResponseEntity deleteAllByRequest(@RequestBody @Valid TechDeleteAllRequestDto techDeleteAllRequestDto) {
        techService.deleteAllByRequest(techDeleteAllRequestDto);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/admin/tech")
    public ResponseEntity post(@RequestBody @Valid TechSaveRequestDto techSaveRequestDto) {
        techService.create(techSaveRequestDto);
        URI readAllLocation = ServletUriComponentsBuilder.fromCurrentContextPath().path("/tech").build().toUri();
        return ResponseEntity.created(readAllLocation).build();
    }

    @PutMapping("/admin/tech/{id}")
    public ResponseEntity<TechResponseDto> update(@PathVariable Long id, @RequestBody @Valid TechSaveRequestDto techSaveRequestDto) {
        TechResponseDto techResponseDto = techService.update(id, techSaveRequestDto);
        return ResponseEntity.ok(techResponseDto);
    }

    @DeleteMapping("/admin/tech/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        techService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
