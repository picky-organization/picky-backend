package network.picky.web.project.exception;

import lombok.Getter;
import network.picky.web.common.exception.BadRequestException;

@Getter
public class ProjectBadRequestException extends BadRequestException {
    private static final String message = "잘못된 요청입니다.";
    public ProjectBadRequestException(){
        super(message);
    }

    public ProjectBadRequestException(String message){
        super(message);
    }
}