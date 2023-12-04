package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFoundException(NotFoundException e) {
        Map<String, String> exceptions = new HashMap<>();
        exceptions.put("errorMessage", e.getLocalizedMessage());
        log.error(e.getLocalizedMessage());
        return exceptions;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidException(MethodArgumentNotValidException e) {
        Map<String, String> exceptions = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            exceptions.put(error.getField(), error.getDefaultMessage());
            log.error("Поле " + error.getField() + " не прошло валидацию. Причина: " + error.getDefaultMessage());
        }

        return exceptions;
    }
}