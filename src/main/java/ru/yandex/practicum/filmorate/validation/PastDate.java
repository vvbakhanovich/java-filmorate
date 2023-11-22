package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.time.LocalDate;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LocalDateValidator.class)
public @interface PastDate {

    String date() default "28/12/1895";
    public String message() default "Дата релиза должна быть позже 28/12/1895";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
