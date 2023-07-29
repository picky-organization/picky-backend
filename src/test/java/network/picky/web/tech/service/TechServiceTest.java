package network.picky.web.tech.service;

import network.picky.web.tech.domain.Tech;
import network.picky.web.tech.dto.TechDeleteAllRequestDto;
import network.picky.web.tech.dto.TechResponseDto;
import network.picky.web.tech.dto.TechSaveRequestDto;
import network.picky.web.tech.exception.TechExistsException;
import network.picky.web.tech.exception.TechNotFoundException;
import network.picky.web.tech.repository.TechRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TechServiceTest {
    TechRepository techRepository = Mockito.mock(TechRepository.class, Mockito.RETURNS_DEEP_STUBS);
    TechService techService = new TechService(techRepository);

    @Test
    @DisplayName("readAll : 정상")
    public void testReadAll() {
        //given
        List<Tech> teches = new ArrayList<>();
        teches.add(new Tech(1L, "tech1"));
        teches.add(new Tech(2L, "tech2"));
        teches.add(new Tech(3L, "tech3"));
        teches.add(new Tech(4L, "tech4"));
        Mockito.when(techRepository.findAll()).thenReturn(teches);

        //when
        List<TechResponseDto> techDtos = techService.readAll();

        //then
        List<Long> returnIds = techDtos.stream().map(value -> value.getId()).collect(Collectors.toList());
        List<Long> techIds = teches.stream().map(value -> value.getId()).collect(Collectors.toList());

        assertEquals(returnIds, techIds);
    }

    @Test
    @DisplayName("readAll : 읽어온 값이 비어 있을 경우")
    public void testReadAllEmpty() {
        //given
        List<Tech> teches = new ArrayList<>();
        Mockito.when(techRepository.findAll()).thenReturn(teches);

        //when
        List<TechResponseDto> techDtos = techService.readAll();

        //then
        assertEquals(techDtos, Collections.emptyList());
    }

    @Test
    @DisplayName("deleteAlByRequest : 정상")
    public void testDeleteAllByRequest() {
        //given
        List<Long> ids = LongStream.range(1, 5).boxed().toList();
        TechDeleteAllRequestDto techDeleteAllRequestDto = new TechDeleteAllRequestDto(ids);

        List<Tech> teches = ids.stream().map(n -> new Tech(n, "tech" + n)).collect(Collectors.toList());
        Mockito.when(techRepository.findAllById(ids)).thenReturn(teches);
        Mockito.when(techRepository.count()).thenReturn(teches.stream().count());

        //when
        techService.deleteAllByRequest(techDeleteAllRequestDto);

        //then
        Mockito.verify(techRepository).deleteAll(teches);
    }

    @Test
    @DisplayName("deleteAlByRequest : ids 길이가 초과 했을떄")
    public void deleteAllByRequestTestIdsTooLong() {
        //given
        List<Long> ids = LongStream.range(1, 100000).boxed().toList();
        TechDeleteAllRequestDto techDeleteAllRequestDto = new TechDeleteAllRequestDto(ids);

        List<Long> realIds = LongStream.range(1, 5).boxed().toList();
        List<Tech> teches = realIds.stream().map(n -> new Tech(n, "tech" + n)).collect(Collectors.toList());
        Mockito.when(techRepository.findAllById(ids)).thenReturn(teches);
        Mockito.when(techRepository.count()).thenReturn(teches.stream().count());

        //when
        Executable excute = () -> techService.deleteAllByRequest(techDeleteAllRequestDto);

        //then
        assertThrows(TechNotFoundException.class, excute);
    }

    @Test
    @DisplayName("deleteAlByRequest : 존재하지 않는 기술을 삭제요청 할때")
    public void deleteAllByRequestTestTechNotExists() {
        //given
        List<Long> ids = LongStream.range(2, 6).boxed().toList();
        TechDeleteAllRequestDto techDeleteAllRequestDto = new TechDeleteAllRequestDto(ids);

        List<Long> realIds = LongStream.range(1, 5).boxed().toList();
        List<Tech> teches = realIds.stream().map(n -> new Tech(n, "tech" + n)).collect(Collectors.toList());
        Mockito.when(techRepository.findAllById(ids)).thenReturn(teches);
        Mockito.when(techRepository.count()).thenReturn(teches.stream().count());

        //when
        Executable excute = () -> techService.deleteAllByRequest(techDeleteAllRequestDto);

        //then
        assertThrows(TechNotFoundException.class, excute);
    }


    @Test
    @DisplayName("create : 정상")
    public void create() {
        //given
        final String techName = "tech";
        TechSaveRequestDto saveRequestDto = new TechSaveRequestDto(techName);

        Mockito.when(techRepository.findByName(saveRequestDto.getName()).isPresent()).thenReturn(false);

        Long createdTechId = 1L;
        Tech tech = new Tech(createdTechId, techName);
        Mockito.when(techRepository.save(Mockito.any(Tech.class))).thenReturn(tech);

        //when
        TechResponseDto techResponseDto = techService.create(saveRequestDto);

        //then
        assertEquals(createdTechId, techResponseDto.getId());
        assertEquals(techName, techResponseDto.getName());
    }

    @Test
    @DisplayName("create : 이미 존재하는 기술인 경우")
    public void createTestTechAlreadyExists() {
        //given
        final String techName = "tech";
        TechSaveRequestDto saveRequestDto = new TechSaveRequestDto(techName);

        Mockito.when(techRepository.findByName(saveRequestDto.getName()).isPresent()).thenReturn(true);

        //when
        Executable executable = () -> techService.create(saveRequestDto);

        //then
        assertThrows(TechExistsException.class, executable);
    }

    @Test
    @DisplayName("update : 정상")
    public void update() {
        //given
        final Long id = 1L;
        final String techName = "tech";

        Tech tech = new Tech(id, techName);
        Mockito.when(techRepository.findById(id)).thenReturn(Optional.of(tech));
        Mockito.when(techRepository.save(tech)).thenReturn(tech);

        final String newTechName = "new_tech";
        TechSaveRequestDto saveRequestDto = new TechSaveRequestDto(newTechName);

        //when
        TechResponseDto techResponseDto = techService.update(id, saveRequestDto);

        //then
        assertEquals(tech.getName(), techResponseDto.getName());
    }

    @Test
    @DisplayName("update : 기술을 찾을 수 없음")
    public void updateTestTechNotFound() {
        //given
        final Long id = 1L;
        final String newTechName = "new_tech";
        TechSaveRequestDto saveRequestDto = new TechSaveRequestDto(newTechName);

        Mockito.when(techRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Executable executable = () -> techService.update(id, saveRequestDto);

        //then
        assertThrows(TechNotFoundException.class, executable);
    }

    @Test
    @DisplayName("delete : 정상")
    public void delete() {
        //given
        final Long id = 1L;
        final String techName = "tech";
        Tech tech = new Tech(id, techName);
        Mockito.when(techRepository.findById(id)).thenReturn(Optional.of(tech));

        //when
        techService.delete(id);

        //then
        Mockito.verify(techRepository).delete(tech);

    }

    @Test
    @DisplayName("delete : 기술을 찾을 수 없음")
    public void deleteTestTechNotFound() {
        //given
        final Long id = 1L;
        Mockito.when(techRepository.findById(id)).thenReturn(Optional.empty());

        //when
        Executable executable = () -> techService.delete(id);

        //then
        assertThrows(TechNotFoundException.class, executable);

    }


}