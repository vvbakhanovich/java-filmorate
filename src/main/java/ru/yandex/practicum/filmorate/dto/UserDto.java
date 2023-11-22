package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserDto {
    private String email; //электронная почта
    private String login; //логин пользователя
    private String name; //имя для отображения
    private LocalDate birthday; //дата рождения
}
