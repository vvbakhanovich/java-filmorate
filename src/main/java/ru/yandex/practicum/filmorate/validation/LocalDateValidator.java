package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateValidator implements ConstraintValidator<PastDate, LocalDate> {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate date;

    @Override
    public void initialize(PastDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        date = LocalDate.parse(constraintAnnotation.date(), formatter);
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {

        return localDate.isAfter(date);
    }
}
