package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

public final class FilmMapper {
    private FilmMapper() {

    }

    public static FilmDto toDto(Film film) {
        return new FilmDto(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration());
    }

    public static Film toModel(FilmDto filmDto) {
        return new Film(filmDto.getId(), filmDto.getName(), filmDto.getDescription(), filmDto.getReleaseDate(),
                filmDto.getDuration());
    }
}
