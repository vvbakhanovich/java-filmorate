package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.util.Constants;

import java.time.LocalDate;

public class LocalDateValidator implements ConstraintValidator<PastDate, LocalDate> {
    LocalDate date;

    @Override
    public void initialize(PastDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        date = LocalDate.parse(constraintAnnotation.date(), Constants.DATE_FORMATTER);
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {

        return localDate.isAfter(date);
    }
}
