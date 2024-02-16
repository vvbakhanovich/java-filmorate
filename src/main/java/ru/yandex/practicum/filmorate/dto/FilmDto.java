package ru.yandex.practicum.filmorate.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.validation.PastDate;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilmDto {
    private long id;
    @NotBlank(message = "Название не может быть пустым.")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания: 200 символов")
    private String description;
    @PastDate(date = "1895-12-28")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть больше нуля")
    private int duration;
    private Mpa mpa;
    private final LinkedHashSet<Genre> genres = new LinkedHashSet<>();
    private final LinkedHashSet<Director> directors = new LinkedHashSet<>();
    private double rating;
}
