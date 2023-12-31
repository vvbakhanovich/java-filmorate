package ru.yandex.practicum.filmorate.validation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LocalDateValidator.class)
public @interface PastDate {

    String date();

    String message() default "Введите более позднюю дату.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
