package ru.yandex.practicum.filmorate.validation;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.dto.FilmDto;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.validation.ValidationTestUtils.VALIDATOR;
import static ru.yandex.practicum.filmorate.validation.ValidationTestUtils.dtoHasErrorMessage;

public class FilmValidationTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "     "})
    @DisplayName("Проверка невозможности добавить фильм с пустым названием")
    public void createFilmWithoutName(String name) {
        FilmDto filmDto = FilmDto.builder()
                .id(1)
                .name(name)
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2000, 2, 13))
                .duration(113)
                .build();

        assertTrue(dtoHasErrorMessage(filmDto, "Название не может быть пустым."));

    }

    @Test
    @DisplayName("Проверка невозможности добавить описание длиной более 200 символов")
    public void createFilmWithLongDescription() {
        String longDescription = StringUtils.repeat("*", 201);
        FilmDto filmDto = FilmDto.builder()
                .id(1)
                .name("Фильм")
                .description(longDescription)
                .releaseDate(LocalDate.of(2000, 2, 13))
                .duration(113)
                .build();

        assertTrue(dtoHasErrorMessage(filmDto, "Максимальная длина описания: 200 символов"));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 12, 50, 100, 200})
    @DisplayName("Проверка добавления описания разрешенной длины")
    public void createFilmWithDescription(int length) {
        String longDescription = StringUtils.repeat("*", length);
        FilmDto filmDto = FilmDto.builder()
                .id(1)
                .name("Фильм")
                .description(longDescription)
                .releaseDate(LocalDate.of(2000, 2, 13))
                .duration(113)
                .build();

        assertTrue(VALIDATOR.validate(filmDto).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1700-01-01", "1895-12-28"})
    @DisplayName("Проверка невозможности добавление даты релиза раньше 12 декабря 1895 года")
    public void createFilmWithInvalidReleaseDate(String date) {
        LocalDate releaseDate = LocalDate.parse(date);
        FilmDto filmDto = FilmDto.builder()
                .id(1)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(releaseDate)
                .duration(113)
                .build();

        assertTrue(dtoHasErrorMessage(filmDto, "Введите более позднюю дату."));

    }

    @ParameterizedTest
    @ValueSource(strings = {"1895-12-29", "2023-11-23"})
    @DisplayName("Проверка добавления фильма с разрешенным значением даты релиза")
    public void createFilmWithValidReleaseDate(String date) {
        LocalDate releaseDate = LocalDate.parse(date);
        FilmDto filmDto = FilmDto.builder()
                .id(1)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(releaseDate)
                .duration(113)
                .build();

        assertTrue(VALIDATOR.validate(filmDto).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -13, -5, 0})
    @DisplayName("Проверка невозможности добавить фильм с длительностью, меньшей или равной нулю")
    public void createFilmWithInvalidDuration(int duration) {
        FilmDto filmDto = FilmDto.builder()
                .id(1)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2000, 2, 13))
                .duration(duration)
                .build();

        assertTrue(dtoHasErrorMessage(filmDto, "Продолжительность должна быть больше нуля"));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 13, 155, Integer.MAX_VALUE})
    @DisplayName("Проверка добавления фильма с разрешенным значением длительности")
    public void createFilmWithValidDuration(int duration) {
        FilmDto filmDto = FilmDto.builder()
                .id(1)
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.of(2000, 2, 13))
                .duration(duration)
                .build();

        assertTrue(VALIDATOR.validate(filmDto).isEmpty());
    }

    @Test
    @DisplayName("Проверка добавления фильма с неразрешенным названием, описанием, датой релиза длительностью")
    public void createFilmWithInvalidNameDescriptionReleaseDateAndDuration() {
        String longDescription = StringUtils.repeat("*", 201);
        FilmDto filmDto = FilmDto.builder()
                .id(1)
                .name("")
                .description(longDescription)
                .releaseDate(LocalDate.of(1777, 2, 13))
                .duration(-13)
                .build();

        assertAll(
                () -> assertTrue(dtoHasErrorMessage(filmDto, "Название не может быть пустым.")),
                () -> assertTrue(dtoHasErrorMessage(filmDto, "Максимальная длина описания: 200 символов")),
                () -> assertTrue(dtoHasErrorMessage(filmDto, "Введите более позднюю дату.")),
                () -> assertTrue(dtoHasErrorMessage(filmDto, "Продолжительность должна быть больше нуля"))
        );
    }
}
