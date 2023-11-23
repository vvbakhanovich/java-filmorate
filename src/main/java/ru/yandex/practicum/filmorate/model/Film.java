package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    private long id; //целочисленный идентификатор
    private String name; //название
    private String description; //описание
    private LocalDate releaseDate; //дата релиза
    private int duration; //продолжительность фильма
}
