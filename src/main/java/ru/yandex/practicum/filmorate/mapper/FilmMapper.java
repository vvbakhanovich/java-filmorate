package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

@UtilityClass
public class FilmMapper {

    public static FilmDto toDto(Film film) {
        FilmDto filmDto = new FilmDto(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration());
        filmDto.getLikes().addAll(film.getLikes());
        return filmDto;
    }

    public static Film toModel(FilmDto filmDto) {
        Film film = new Film(filmDto.getId(), filmDto.getName(), filmDto.getDescription(), filmDto.getReleaseDate(),
                filmDto.getDuration());
        film.getLikes().addAll(filmDto.getLikes());
        return film;
    }
}
