package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class User {
    private long id; //целочисленный идентификатор
    private String email; //электронная почта
    private String login; //логин пользователя
    private String name; //имя для отображения
    private LocalDate birthday; //дата рождения
    private final Map<Long, Friendship> friends = new LinkedHashMap<>(); //список друзей
}
