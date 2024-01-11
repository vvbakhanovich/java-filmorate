package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

@UtilityClass
public class FilmMapper {

    public static FilmDto toDto(Film film) {
        FilmDto filmDto = FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(film.getMpa())
                .likes(film.getLikes())
                .build();
        filmDto.getGenres().addAll(film.getGenres());
        return filmDto;
    }

    public static Film toModel(FilmDto filmDto) {
        Film film = Film.builder()
                .id(filmDto.getId())
                .name(filmDto.getName())
                .description(filmDto.getDescription())
                .releaseDate(filmDto.getReleaseDate())
                .duration(filmDto.getDuration())
                .mpa(filmDto.getMpa())
                .likes(filmDto.getLikes())
                .build();
        film.getGenres().addAll(filmDto.getGenres());
        return film;
    }
}
