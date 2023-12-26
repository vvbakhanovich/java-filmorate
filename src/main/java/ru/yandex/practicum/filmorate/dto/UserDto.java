package ru.yandex.practicum.filmorate.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Friendship;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private final Map<Long, Friendship> friends = new LinkedHashMap<>(); //список друзей
}
