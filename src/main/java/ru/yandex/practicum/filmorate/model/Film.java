package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Film {
    private long id; //целочисленный идентификатор
    private String name; //название
    private String description; //описание
    private LocalDate releaseDate; //дата релиза
    private int duration; //продолжительность фильма
    private Mpa mpa; //возрастной рейтинг
    private final LinkedHashSet<Genre> genres = new LinkedHashSet<>(); //жанры
    private final LinkedHashSet<Director> directors = new LinkedHashSet<>(); //режиссеры
    private double rating; //количество лайков от пользователей
}
