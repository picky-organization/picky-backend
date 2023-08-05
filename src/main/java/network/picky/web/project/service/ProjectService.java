package network.picky.web.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import network.picky.web.category.domain.Category;
import network.picky.web.category.exception.CategoryNotFoundException;
import network.picky.web.category.repository.CategoryRepository;
import network.picky.web.member.domain.Member;
import network.picky.web.member.exception.MemberNotFoundException;
import network.picky.web.member.repository.MemberRepository;
import network.picky.web.project.domain.Project;
import network.picky.web.project.domain.ProjectCategory;
import network.picky.web.project.domain.ProjectTech;
import network.picky.web.project.dto.ProjectAllResponseDto;
import network.picky.web.project.dto.ProjectResponseDto;
import network.picky.web.project.dto.ProjectSaveRequestDto;
import network.picky.web.project.exception.ProjectBadRequestException;
import network.picky.web.project.exception.ProjectNotFoundException;
import network.picky.web.project.repository.ProjectCategoryRepository;
import network.picky.web.project.repository.ProjectRepository;
import network.picky.web.project.repository.ProjectTechRepository;
import network.picky.web.tech.domain.Tech;
import network.picky.web.tech.exception.TechNotFoundException;
import network.picky.web.tech.repository.TechRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class ProjectService {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final CategoryRepository categoryRepository;
    private final TechRepository techRepository;
    private final ProjectCategoryRepository projectCategoryRepository;
    private final ProjectTechRepository projectTechRepository;

    public Page<ProjectAllResponseDto> readAll(Pageable pageable){
        Page<Project> projectPage = projectRepository.findAll(pageable);
        return projectPage.map(ProjectAllResponseDto::new);
    }

    public ProjectResponseDto create(Long memberId, ProjectSaveRequestDto projectSaveRequestDto) throws MemberNotFoundException,CategoryNotFoundException, TechNotFoundException, ProjectBadRequestException {
        Member member =  memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        member.increaseProjectCount();

        List<Category> categories = findAndValidCategories(projectSaveRequestDto.getCategories());
        List<Tech> teches = findAndValidTeches(projectSaveRequestDto.getTeches());

        Project project = projectSaveRequestDto.toEntity(member);
        Project savedProject =  projectRepository.save(project);

        List<ProjectCategory> projectCategories = categories.stream().map(category -> new ProjectCategory(savedProject, category)).collect(Collectors.toList());
        List<ProjectCategory> savedProjectCategories = projectCategoryRepository.saveAll(projectCategories);
        savedProject.updateProjectCategories(savedProjectCategories);

        List<ProjectTech> projectTeches = teches.stream().map(tech -> new ProjectTech(savedProject, tech)).collect(Collectors.toList());
        List<ProjectTech> savedProjectTeches = projectTechRepository.saveAll(projectTeches);
        savedProject.updateProjectTeches(savedProjectTeches);

        return new ProjectResponseDto(savedProject);
    }

    public ProjectResponseDto read(Long id) throws ProjectNotFoundException{
       Project project =  projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);
        project.increaseViewCount();
       return new ProjectResponseDto(project);
    }

    @PostAuthorize("hasRole('ADMIN') || returnObject.getMemberSummary().getId() == principal")
    public ProjectResponseDto update(Long id, ProjectSaveRequestDto projectSaveRequestDto) throws ProjectNotFoundException{
        List<Category> categories = findAndValidCategories(projectSaveRequestDto.getCategories());
        List<Tech> teches = findAndValidTeches(projectSaveRequestDto.getTeches());

        Project project = projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);

        List<ProjectCategory> insertProjectCategories = categories
                .stream()
                .map(category -> new ProjectCategory(project, category))
                .collect(Collectors.toList());
        List<ProjectCategory> deleteProjectCategories = project.getProjectCategories()
                .stream()
                .filter(projectCategory -> !insertProjectCategories.contains(projectCategory))
                .collect(Collectors.toList());
        List<ProjectCategory> appendProjectCategories = insertProjectCategories
                .stream()
                .filter(projectCategory -> !project.getProjectCategories().contains(projectCategory))
                .collect(Collectors.toList());


        List<ProjectTech> insertProjectTeches = teches
                .stream()
                .map(tech -> new ProjectTech(project, tech))
                .collect(Collectors.toList());
        List<ProjectTech> deleteProjectTeches = project.getProjectTeches()
                .stream()
                .filter(projectTech -> !insertProjectTeches.contains(projectTech))
                .collect(Collectors.toList());
        List<ProjectTech> appendProjectTeches = insertProjectTeches
                .stream()
                .filter(projectTech -> !project.getProjectTeches().contains(projectTech))
                .collect(Collectors.toList());


        project.update(projectSaveRequestDto);
        projectRepository.save(project);

        projectCategoryRepository.deleteAll(deleteProjectCategories);
        projectCategoryRepository.saveAll(appendProjectCategories);
        project.updateProjectCategories(insertProjectCategories);

        projectTechRepository.deleteAll(deleteProjectTeches);
        projectTechRepository.saveAll(appendProjectTeches);
        project.updateProjectTeches(insertProjectTeches);
        ProjectResponseDto projectResponseDto = new ProjectResponseDto(project);
        return projectResponseDto;
    }

    @PostAuthorize("hasRole('ADMIN') || returnObject.getMemberSummary().getId() == principal")
    public ProjectResponseDto delete(Long id) throws ProjectNotFoundException{
        Project project =  projectRepository.findById(id).orElseThrow(ProjectNotFoundException::new);
        project.getMember().decreaseProjectCount();
        projectRepository.delete(project);

        ProjectResponseDto responseDto = new ProjectResponseDto(project);
        return responseDto;
    }


    public List<Category> findAndValidCategories(List<Long> categoryIds){
        if(categoryIds == null || categoryIds.size() == 0){
            throw new ProjectBadRequestException("최소 한개의 카테고리가 존재해야 합니다");
        }

        List<Category> findCategories = categoryRepository.findAllById(categoryIds);
        Set<Long> findCategoryIds = findCategories.stream().map(Category::getId).collect(Collectors.toCollection(HashSet::new));
        if(findCategoryIds.containsAll(categoryIds)){
            return findCategories;
        }
        throw new CategoryNotFoundException();
    }

    public List<Tech> findAndValidTeches(List<Long> techIds){
        if(techIds == null || techIds.size()==0){
            return Collections.emptyList();
        }

        List<Tech> findTeches = techRepository.findAllById(techIds);
        Set<Long> findTechIds = findTeches.stream().map(Tech::getId).collect(Collectors.toCollection(HashSet::new));
        if(findTechIds.containsAll(techIds)){
            return findTeches;
        }
        throw new TechNotFoundException();

    }
}
