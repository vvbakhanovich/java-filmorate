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
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Mpa mpa;
    private final LinkedHashSet<Genre> genres = new LinkedHashSet<>();
    private final LinkedHashSet<Director> directors = new LinkedHashSet<>();
    private double rating;
}
