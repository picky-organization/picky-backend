package network.picky.web.project.exception;

import lombok.Getter;
import network.picky.web.common.exception.BadRequestException;

@Getter
public class ProjectCommentBadRequestException extends BadRequestException {
    private static final String message = "잘못된 요청입니다.";
    public ProjectCommentBadRequestException(){
        super(message);
    }

    public ProjectCommentBadRequestException(String message){
        super(message);
    }
}