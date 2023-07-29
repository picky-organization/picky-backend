package network.picky.web.category.exception;

import lombok.Getter;
import network.picky.web.common.exception.ConflictException;

@Getter
public class CategoryExistsException extends ConflictException {
    private static final String message = "이미 카테고리가 존재합니다.";

    public CategoryExistsException() {
        super(message);
    }
}
