package network.picky.web.project.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import network.picky.web.member.domain.Member;
import network.picky.web.member.exception.MemberNotFoundException;
import network.picky.web.member.repository.MemberRepository;
import network.picky.web.project.domain.Project;
import network.picky.web.project.domain.ProjectComment;
import network.picky.web.project.dto.ProjectCommentCreateRequestDto;
import network.picky.web.project.dto.ProjectCommentResponseDto;
import network.picky.web.project.dto.ProjectCommentUpdateRequestDto;
import network.picky.web.project.exception.ProjectCommentBadRequestException;
import network.picky.web.project.exception.ProjectCommentNotFoundException;
import network.picky.web.project.exception.ProjectNotFoundException;
import network.picky.web.project.repository.ProjectCommentRepository;
import network.picky.web.project.repository.ProjectRepository;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ProjectCommentService {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final ProjectCommentRepository projectCommentRepository;

    public List<ProjectCommentResponseDto> readAll(Long projectId, Long parentId) throws ProjectNotFoundException, ProjectCommentNotFoundException {
        Project project = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new);

        List<ProjectComment> projectComments;
        if (parentId == null) {
            projectComments = projectCommentRepository.findAllByProjectAndParentIsNull(project);
        } else {
            ProjectComment parent = projectCommentRepository.findById(parentId).orElseThrow(() -> new ProjectCommentNotFoundException("부모 댓글을 찾을 수 없습니다."));
            projectComments = projectCommentRepository.findAllByParent(parent);
        }

        return projectComments.stream()
                .map(projectComment -> new ProjectCommentResponseDto(projectComment, projectCommentRepository.countByParent(projectComment))).toList();
    }

    public ProjectCommentResponseDto create(Long projectId, Long memberId, ProjectCommentCreateRequestDto projectCommentCreateRequestDto) throws MemberNotFoundException, ProjectNotFoundException, ProjectCommentBadRequestException {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        Project project = projectRepository.findById(projectId).orElseThrow(ProjectNotFoundException::new);
        ProjectComment parent = null;
        if (projectCommentCreateRequestDto.getParentId() != null) {
            parent = projectCommentRepository.findById(projectCommentCreateRequestDto.getParentId()).orElseThrow(ProjectCommentNotFoundException::new);
            if (parent.getParent() != null) {
                throw new ProjectCommentBadRequestException("답글에는 또 답글을 달 수 없습니다.");
            }
        }

        ProjectComment projectComment = projectCommentCreateRequestDto.toEntity(member, project, parent);

        ProjectComment savedComment = projectCommentRepository.save(projectComment);
        savedComment.getProject().increaseCommentCount();
        return new ProjectCommentResponseDto(savedComment);
    }

    public ProjectCommentResponseDto read(Long id) throws ProjectCommentNotFoundException {
        ProjectComment projectComment = projectCommentRepository.findById(id).orElseThrow(ProjectCommentNotFoundException::new);
        int childSize = projectCommentRepository.countByParent(projectComment);
        return new ProjectCommentResponseDto(projectComment, childSize);
    }

    @PostAuthorize("hasRole('ADMIN') || returnObject.getMemberSummary().getId() == principal")
    public ProjectCommentResponseDto update(Long id, ProjectCommentUpdateRequestDto projectCommentUpdateRequestDto) throws ProjectCommentNotFoundException {
        ProjectComment projectComment = projectCommentRepository.findById(id).orElseThrow(ProjectCommentNotFoundException::new);
        projectComment.update(projectCommentUpdateRequestDto);
        ProjectComment updatedProjectComment = projectCommentRepository.save(projectComment);
        int childCount = projectCommentRepository.countByParent(projectComment);
        return new ProjectCommentResponseDto(updatedProjectComment, childCount);
    }

    @PostAuthorize("hasRole('ADMIN') || returnObject.getMemberSummary().getId() == principal")
        public ProjectCommentResponseDto delete(Long id) throws ProjectCommentNotFoundException {
        ProjectComment projectComment = projectCommentRepository.findById(id).orElseThrow(ProjectCommentNotFoundException::new);
        int childCount = projectCommentRepository.countByParent(projectComment);
        if (childCount > 0) {
            projectCommentRepository.deleteAllByParent(projectComment);
            projectComment.getProject().decreaseCommentCount(childCount);
        }
        projectComment.getProject().decreaseCommentCount();
        projectCommentRepository.delete(projectComment);

        ProjectCommentResponseDto projectCommentResponseDto = new ProjectCommentResponseDto(projectComment);
        return projectCommentResponseDto;
    }
}