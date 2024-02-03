package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.FilmDto;

import java.util.Collection;

public interface FilmService {
    FilmDto addFilm(FilmDto filmDto);

    FilmDto updateFilm(FilmDto filmDto);

    Collection<FilmDto> getAllFilms();

    FilmDto getFilmById(long filmId);

    FilmDto likeFilm(long filmId, long userId, int rating);

    FilmDto removeLike(long filmId, long userId);

    void removeFilm(long filmId);

    Collection<FilmDto> getMostPopularFilms(final int count);

    Collection<FilmDto> getFilmsFromDirector(long directorId, String sortBy);
}