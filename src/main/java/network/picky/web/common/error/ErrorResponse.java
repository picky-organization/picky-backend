package network.picky.web.common.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import network.picky.web.common.exception.BaseHttpStatusException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String statusName;
    private final String path;
    private final String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(BaseHttpStatusException ex) {
        String currentPath = ServletUriComponentsBuilder.fromCurrentRequest().toUriString();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ex.getStatus())
                .statusName(ex.getStatusName())
                .path(currentPath)
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(ex.getStatus())
                .body(errorResponse);
    }
}