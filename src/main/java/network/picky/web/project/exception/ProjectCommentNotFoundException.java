package network.picky.web.project.exception;

import lombok.Getter;
import network.picky.web.common.exception.NotFoundException;

@Getter
public class ProjectCommentNotFoundException extends NotFoundException {
    private static final String message = "프로젝트 댓글이 존재하지 않습니다.";

    public ProjectCommentNotFoundException() {
        super(message);
    }
    public ProjectCommentNotFoundException(String message) {
        super(message);
    }
}
