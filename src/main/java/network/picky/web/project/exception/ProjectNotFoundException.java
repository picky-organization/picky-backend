package network.picky.web.project.exception;

import lombok.Getter;
import network.picky.web.common.exception.NotFoundException;

@Getter
public class ProjectNotFoundException extends NotFoundException {
    private static final String message = "프로젝트가 존재하지 않습니다.";

    public ProjectNotFoundException() {
        super(message);
    }
}
