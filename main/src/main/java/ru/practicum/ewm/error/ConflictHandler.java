package ru.practicum.ewm.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@ResponseStatus(HttpStatus.CONFLICT)
@Slf4j
public class ConflictHandler {
    private static final String REASON_INTEGRITY_CONFLICT = "Integrity constraint has been violated.";

    @ExceptionHandler
    public ApiError handleNotFoundException(final DataIntegrityViolationException e) {
        String message = e.getMessage();
        return ApiError.builder()
                .message(message)
                .reason(REASON_INTEGRITY_CONFLICT)
                .status(HttpStatus.CONFLICT.name())
                .timestamp(LocalDateTime.now())
                .build();
    }
}