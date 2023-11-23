package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.PastDate;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class FilmDto {
    private long id;
    @NotBlank(message = "Название не может быть пустым.")
    private String name; //название
    @Size(max = 200, message = "Максимальная длина описания: 200 символов")
    private String description; //описание
    @PastDate(date = "1895-12-28")
    private LocalDate releaseDate; //дата релиза
    @Positive(message = "Продолжительность должна быть больше нуля")
    private int duration; //продолжительность фильма
}
