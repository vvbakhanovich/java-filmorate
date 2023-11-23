package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.dto.UserDto;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.validation.ValidationTestUtils.VALIDATOR;
import static ru.yandex.practicum.filmorate.validation.ValidationTestUtils.dtoHasErrorMessage;

public class UserValidationTest {

    @ParameterizedTest
    @ValueSource(strings = {" ", "test.ru", "   .com", "@test", "@.org", "test@"})
    @DisplayName("Проверка невозможности добавить пользователя с неправильно заданным email")
    public void createUserWithInvalidEmail(String email) {
        UserDto userDto = new UserDto(1, email, "login", "name",
                LocalDate.of(1991, 12, 12));

        assertTrue(dtoHasErrorMessage(userDto, "Некорректный формат электронной почты."));
    }

    @ParameterizedTest
    @ValueSource(strings = {""})
    @DisplayName("Проверка невозможности добавить пользователя с неправильно заданным email")
    public void createUserWithEmptyEmail(String email) {
        UserDto userDto = new UserDto(1, email, "login", "name",
                LocalDate.of(1991, 12, 12));

        assertTrue(dtoHasErrorMessage(userDto, "Адрес электронной почты не может быть пустым."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@test.ru", "test1234@test1234.org", "test.test@test.com", "test-test@test.pom"})
    @DisplayName("Проверка добавления email с разрешенным значением")
    public void createUserWithValidEmail(String email) {
        UserDto userDto = new UserDto(1, email, "login", "name",
                LocalDate.of(1991, 12, 12));

        assertTrue(VALIDATOR.validate(userDto).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  ", "     "})
    @DisplayName("Проверка невозможности добавить пользователя с пустым или состоящим из пробелов логином")
    public void createUserWithInvalidLogin(String login) {
        UserDto userDto = new UserDto(1, "test@test.ru", login, "name",
                LocalDate.of(1991, 12, 12));

        assertTrue(dtoHasErrorMessage(userDto, "Логин не может быть пустым и содержать пробелы."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023-11-24", "2077-12-12"})
    @DisplayName("Проверка невозможности добавления пользователя, дата рождения которого в будущем")
    public void createUserWithInvalidBirthday(String birthdayString) {
        LocalDate birthday = LocalDate.parse(birthdayString);
        UserDto userDto = new UserDto(1, "test@test.ru", "login", "name",
                birthday);

        assertTrue(dtoHasErrorMessage(userDto, "Дата рождения не может быть в будущем."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2023-11-23", "1700-12-12"})
    @DisplayName("Проверка добавления пользователя с разрешенным значением даты рождения")
    public void createUserWithValidBirthday(String birthdayString) {
        LocalDate birthday = LocalDate.parse(birthdayString);
        UserDto userDto = new UserDto(1, "test@test.ru", "login", "name",
                birthday);

        assertTrue(VALIDATOR.validate(userDto).isEmpty());
    }

    @Test
    @DisplayName("Проверка добавления пользователя с неразрешенным email, логином и датой рождения")
    public void createUserWithInvalidEmailLoginAndBirthday() {
        UserDto userDto = new UserDto(1, "test", "", "name",
                LocalDate.of(2222, 10, 10));

        assertAll(
                () -> assertTrue(dtoHasErrorMessage(userDto, "Некорректный формат электронной почты.")),
                () -> assertTrue(dtoHasErrorMessage(userDto, "Логин не может быть пустым и содержать пробелы.")),
                () -> assertTrue(dtoHasErrorMessage(userDto, "Дата рождения не может быть в будущем."))
        );
    }


}
