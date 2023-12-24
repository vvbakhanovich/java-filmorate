package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

@UtilityClass
public class FilmMapper {

    public static FilmDto toDto(Film film) {
        FilmDto filmDto = new FilmDto(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpaRating());
        filmDto.getLikes().addAll(film.getLikes());
        filmDto.getGenres().addAll(film.getGenres());
        return filmDto;
    }

    public static Film toModel(FilmDto filmDto) {
        Film film = new Film(filmDto.getId(), filmDto.getName(), filmDto.getDescription(), filmDto.getReleaseDate(),
                filmDto.getDuration(), filmDto.getMpaRating());
        film.getLikes().addAll(filmDto.getLikes());
        film.getGenres().addAll(filmDto.getGenres());
        return film;
    }
}
