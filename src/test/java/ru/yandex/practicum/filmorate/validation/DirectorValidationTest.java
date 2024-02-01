package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.dto.DirectorDto;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.validation.ValidationTestUtils.VALIDATOR;
import static ru.yandex.practicum.filmorate.validation.ValidationTestUtils.dtoHasErrorMessage;

public class DirectorValidationTest {

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "     "})
    @DisplayName("Проверка невозможности добавить режиссера с пустым именем")
    public void createDirectorWithoutName(String name) {
        DirectorDto directorDto = DirectorDto.builder()
                .id(1)
                .name(name)
                .build();

        assertTrue(dtoHasErrorMessage(directorDto, "Имя режиссера не может быть пустым."));

    }

    @Test
    @DisplayName("Проверка невозможности добавить режиссера, если name == null")
    public void createDirectorWithNullName() {
        DirectorDto directorDto = DirectorDto.builder()
                .id(1)
                .name(null)
                .build();

        assertTrue(dtoHasErrorMessage(directorDto, "Имя режиссера не может быть пустым."));
    }

    @Test
    @DisplayName("Проверка добавления режиссера с валидными полями.")
    public void createDirector() {
        DirectorDto directorDto = DirectorDto.builder()
                .id(1)
                .name("Director")
                .build();
        assertTrue(VALIDATOR.validate(directorDto).isEmpty());
    }
}


