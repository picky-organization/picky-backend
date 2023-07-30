package network.picky.web.common.error;

import lombok.extern.slf4j.Slf4j;
import network.picky.web.common.exception.BadRequestException;
import network.picky.web.common.exception.ConflictException;
import network.picky.web.common.exception.NotFoundException;
import network.picky.web.common.exception.UnprocessableEntityException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleCategoryExistsException(ConflictException e) {
        return ErrorResponse.toResponseEntity(e);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(NotFoundException e) {
        return ErrorResponse.toResponseEntity(e);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ErrorResponse> handleUnprocessableEntityException(UnprocessableEntityException e){
        return ErrorResponse.toResponseEntity(e);
    }

    // 비어있는 request 일 때
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex){
        BadRequestException badRequestException = new BadRequestException("지원하지않는 요청이거나 형식이 올바르지 않습니다.");
        return ErrorResponse.toResponseEntity(badRequestException);
    }

    // /project/wrong 같이 {id} 부분이 Long이 아닐 거나 Long 최대크기를 초과할 때
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex){
        BadRequestException badRequestException = new BadRequestException("지원하지 않는 id 형식 입니다.");
        return ErrorResponse.toResponseEntity(badRequestException);
    }

    // request를 dto로 변환할 수 없을 때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}