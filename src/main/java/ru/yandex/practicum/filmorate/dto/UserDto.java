package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserDto {
    @Email(message = "Некорректный формат электронной почты.")
    private String email; //электронная почта
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы")
    private String login; //логин пользователя
    private String name; //имя для отображения
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday; //дата рождения
}
