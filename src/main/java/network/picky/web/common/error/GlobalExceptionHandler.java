package network.picky.web.common.error;

import lombok.extern.slf4j.Slf4j;
import network.picky.web.common.exception.BadRequestException;
import network.picky.web.common.exception.ConflictException;
import network.picky.web.common.exception.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConflictException.class)
    protected ResponseEntity<ErrorResponse> handleCategoryExistsException(ConflictException e) {
        return ErrorResponse.toResponseEntity(e);
    }

    @ExceptionHandler(NotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleCategoryNotFoundException(NotFoundException e) {
        return ErrorResponse.toResponseEntity(e);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) throws IOException {
        BadRequestException badRequestException = new BadRequestException("요청한 문법 형식이 맞지 않습니다.");
        return ErrorResponse.toResponseEntity(badRequestException);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) throws IOException {
        BadRequestException badRequestException = new BadRequestException("입력하신 값의 검증에 실패했습니다.");
        return ErrorResponse.toResponseEntity(badRequestException);
    }
}
