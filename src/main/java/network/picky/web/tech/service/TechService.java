package network.picky.web.tech.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import network.picky.web.tech.domain.Tech;
import network.picky.web.tech.dto.TechDeleteAllRequestDto;
import network.picky.web.tech.dto.TechResponseDto;
import network.picky.web.tech.dto.TechSaveRequestDto;
import network.picky.web.tech.exception.TechExistsException;
import network.picky.web.tech.exception.TechNotFoundException;
import network.picky.web.tech.repository.TechRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class TechService {
    private final TechRepository techRepository;

    public List<TechResponseDto> readAll() {
        List<Tech> teches = techRepository.findAll();
        return teches.stream().map(tech -> tech.toResponseDto()).collect(Collectors.toList());
    }

    public void deleteAllByRequest(TechDeleteAllRequestDto techDeleteAllRequestDto) throws TechNotFoundException {
        if (techDeleteAllRequestDto.getIds().size() > techRepository.count()) {
            throw new TechNotFoundException();
        }

        List<Tech> teches = techRepository.findAllById(techDeleteAllRequestDto.getIds());
        if (requestTechesExistsAll(techDeleteAllRequestDto, teches)) {
            techRepository.deleteAll(teches);
        } else {
            throw new TechNotFoundException();
        }
    }

    public TechResponseDto create(TechSaveRequestDto techSaveRequestDto) throws TechExistsException {
        if (techRepository.findByName(techSaveRequestDto.getName()).isPresent()) {
            throw new TechExistsException();
        }
        Tech tech = techSaveRequestDto.toEntity();
        tech = techRepository.save(tech);
        return tech.toResponseDto();
    }

    public TechResponseDto update(Long id, TechSaveRequestDto techSaveRequestDto) throws TechNotFoundException {
        network.picky.web.tech.domain.Tech tech = techRepository.findById(id).orElseThrow(TechNotFoundException::new);
        tech.updateName(techSaveRequestDto.getName());
        tech = techRepository.save(tech);
        return tech.toResponseDto();
    }

    public void delete(Long id) throws TechNotFoundException {
        Tech tech = techRepository.findById(id).orElseThrow(TechNotFoundException::new);
        techRepository.delete(tech);
    }

    private boolean requestTechesExistsAll(TechDeleteAllRequestDto techDeleteAllRequestDto, List<Tech> findTeches) {
        List<Long> findTechIds = findTeches.stream().map(tech -> tech.getId()).collect(Collectors.toList());
        List<Long> requestTechIds = techDeleteAllRequestDto.getIds();
        return findTechIds.containsAll(requestTechIds);
    }
}