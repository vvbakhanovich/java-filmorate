package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class ValidationTestUtils {
    public static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private ValidationTestUtils() {

    }

    public static <T> boolean dtoHasErrorMessage(T dto, @NotNull String message) {
        Set<ConstraintViolation<T>> errors = VALIDATOR.validate(dto);
        return errors.stream().map(ConstraintViolation::getMessage).anyMatch(message::equals);
    }
}
