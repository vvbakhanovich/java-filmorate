package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @Email(message = "Некорректный формат электронной почты.", regexp = "^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    @NotEmpty(message = "Адрес электронной почты не может быть пустым.")
    private String email; //электронная почта
    @NotBlank(message = "Логин не может быть пустым и содержать пробелы.")
    private String login; //логин пользователя
    private String name; //имя для отображения
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday; //дата рождения
}
