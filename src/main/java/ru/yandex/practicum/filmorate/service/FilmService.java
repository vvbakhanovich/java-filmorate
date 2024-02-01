package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.FilmDto;

import java.util.Collection;

public interface FilmService {
    FilmDto addFilm(FilmDto filmDto);

    FilmDto updateFilm(FilmDto filmDto);

    Collection<FilmDto> getAllFilms();

    FilmDto getFilmById(long filmId);

    FilmDto likeFilm(long filmId, long userId);

    FilmDto removeLike(long filmId, long userId);

    void removeFilm(long filmId);

    Collection<FilmDto> getMostPopularFilms(final int count);
}