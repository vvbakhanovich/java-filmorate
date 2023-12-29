package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    private long id; //целочисленный идентификатор
    private String name; //название
    private String description; //описание
    private LocalDate releaseDate; //дата релиза
    private int duration; //продолжительность фильма
    private Mpa mpa; //возрастной рейтинг
    private final List<Genre> genres = new ArrayList<>(); //жанры
    private final Set<Long> likes = new LinkedHashSet<>(); //список лайков от пользователей
}
