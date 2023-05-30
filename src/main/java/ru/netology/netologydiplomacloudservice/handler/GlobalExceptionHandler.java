package ru.netology.netologydiplomacloudservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ru.netology.netologydiplomacloudservice.api.dto.ErrorDto;
import ru.netology.netologydiplomacloudservice.exception.IncorrectInputDataException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        MaxUploadSizeExceededException.class,
        IncorrectInputDataException.class,
        MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorDto> handleClientException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDeniedException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleFileProcessingException(Exception ex) {
        log.error(ex.getMessage());
        return ResponseEntity.internalServerError().body(new ErrorDto(ex.getMessage()));
    }
}
