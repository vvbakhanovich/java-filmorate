package ru.yandex.practicum.filmorate.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(FilmNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleFilmNotFoundException(FilmNotFoundException e) {
        Map<String, String> exceptions = new HashMap<>();
        exceptions.put("errorMessage", e.getLocalizedMessage());
        return exceptions;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidException(MethodArgumentNotValidException e) {
        Map<String, String> exceptions = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
                    exceptions.put(fieldError.getField(), fieldError.getDefaultMessage());
                }
        );

        exceptions.put("errorMessage", e.getMessage());
        return exceptions;
    }
}