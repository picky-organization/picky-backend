package network.picky.web.category.exception;

import lombok.Getter;
import network.picky.web.common.exception.NotFoundException;

@Getter
public class CategoryNotFoundException extends NotFoundException {
    private static final String message = "카테고리가 존재하지 않습니다.";

    public CategoryNotFoundException() {
        super(message);
    }
}
