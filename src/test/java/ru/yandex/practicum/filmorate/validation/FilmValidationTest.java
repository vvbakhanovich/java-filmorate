package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmDto;

import java.time.LocalDate;

public class FilmValidationTest {

    @Test
    @DisplayName("Проверка невозможности добавить фильм с пустым названием")
    public void createFilmWithoutName() {
        FilmDto filmDto = new FilmDto("", "Описание фильма",
                LocalDate.of(2000, 2, 13),113);

       Assertions.assertTrue(ValidationTestUtils.dtoHasErrorMessage(filmDto, "Название не может быть пустым."));

    }

    @Test
    @DisplayName("Проверка невозможности добавить фильм только с пробелами в названии")
    public void createFilmWithWhiteSpacesOnly() {
        FilmDto filmDto = new FilmDto("    ", "Описание фильма",
                LocalDate.of(2000, 2, 13), 113);

        Assertions.assertTrue(ValidationTestUtils.dtoHasErrorMessage(filmDto, "Название не может быть пустым."));

    }


}
