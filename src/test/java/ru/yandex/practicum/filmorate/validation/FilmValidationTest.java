package ru.yandex.practicum.filmorate.validation;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.dto.FilmDto;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.validation.ValidationTestUtils.VALIDATOR;
import static ru.yandex.practicum.filmorate.validation.ValidationTestUtils.dtoHasErrorMessage;

public class FilmValidationTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "     "})
    @DisplayName("Проверка невозможности добавить фильм с пустым названием")
    public void createFilmWithoutName(String name) {
        FilmDto filmDto = new FilmDto(name, "Описание фильма",
                LocalDate.of(2000, 2, 13),113);

       assertTrue(dtoHasErrorMessage(filmDto, "Название не может быть пустым."));

    }

    @Test
    @DisplayName("Проверка невозможности добавить описание длиной более 200 символов")
    public void createFilmWithLongDescription() {
        String longDescription = StringUtils.repeat("*", 201);
        FilmDto filmDto = new FilmDto("Фильм", longDescription,
                LocalDate.of(2000, 2, 13), 113);

        assertTrue(dtoHasErrorMessage(filmDto,
                "Максимальная длина описания: 200 символов"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 12, 50, 100, 200})
    @DisplayName("Проверка добавления описания разрешимой длины")
    public void createFilmWithDescription(int length) {
        String longDescription = StringUtils.repeat("*", length);
        FilmDto filmDto = new FilmDto("Фильм", longDescription,
                LocalDate.of(2000, 2, 13), 113);

        assertTrue(VALIDATOR.validate(filmDto).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1700-01-01", "1895-12-28"})
    @DisplayName("Проверка невозможности добавление даты релиза раньше 12 декабря 1895 года")
    public void createFilmWithInvalidReleaseDate(String date) {
        LocalDate releaseDate = LocalDate.parse(date);
        FilmDto filmDto = new FilmDto("Фильм", "Описание фильма", releaseDate, 113);

        assertTrue(dtoHasErrorMessage(filmDto, "Введите более позднюю дату."));

    }

    @ParameterizedTest
    @ValueSource(strings = {"1895-12-29", "2023-11-23"})
    @DisplayName("Проверка добавления фильма с разрешенным значением даты релиза")
    public void createFilmWithValidReleaseDate(String date) {
        LocalDate releaseDate = LocalDate.parse(date);
        FilmDto filmDto = new FilmDto("Фильм", "Описание фильма", releaseDate, 113);

        assertTrue(VALIDATOR.validate(filmDto).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -13, -5, 0})
    @DisplayName("Проверка невозможности добавить фильм с длительностью, меньшей или равной нулю")
    public void createFilmWithInvalidDuration(int duration) {
        FilmDto filmDto = new FilmDto("Фильм", "Описание фильма",
                LocalDate.of(2000, 2, 13),duration);

        assertTrue(dtoHasErrorMessage(filmDto, "Продолжительность должна быть больше нуля"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 13, 155, Integer.MAX_VALUE})
    @DisplayName("Проверка добавления фильма с разрешенным значением длительности")
    public void createFilmWithValidDuration(int duration) {
        FilmDto filmDto = new FilmDto("Фильм", "Описание фильма",
                LocalDate.of(2000, 2, 13),duration);

        assertTrue(VALIDATOR.validate(filmDto).isEmpty());
    }
}
