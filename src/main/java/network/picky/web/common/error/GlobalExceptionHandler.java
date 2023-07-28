package network.picky.web.common.error;

import lombok.extern.slf4j.Slf4j;
import network.picky.web.category.exception.CategoryExistsException;
import network.picky.web.category.exception.CategoryNotFoundException;
import network.picky.web.common.exception.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CategoryExistsException.class)
    protected ResponseEntity<ErrorResponse> handleCategoryExistsException(CategoryExistsException e) {
        return ErrorResponse.toResponseEntity(e);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleCategoryNotFoundException(CategoryNotFoundException e) {
        return ErrorResponse.toResponseEntity(e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) throws IOException {
        log.debug(ex.getHttpInputMessage().toString());
        log.debug(ex.getHttpInputMessage().getBody().toString());
        BadRequestException badRequestException = new BadRequestException("요청한 문법 형식이 맞지 않습니다.");
        return ErrorResponse.toResponseEntity(badRequestException);
    }
}