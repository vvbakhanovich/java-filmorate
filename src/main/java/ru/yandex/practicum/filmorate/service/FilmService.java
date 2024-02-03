package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmSearchDto;

import java.util.Collection;

public interface FilmService {
    FilmDto addFilm(FilmDto filmDto);

    FilmDto updateFilm(FilmDto filmDto);

    Collection<FilmDto> getAllFilms();

    FilmDto getFilmById(long filmId);

    FilmDto likeFilm(long filmId, long userId, int rating);

    FilmDto removeLike(long filmId, long userId);

    void removeFilm(long filmId);

    Collection<FilmDto> getFilmsFromDirector(long directorId, String sortBy);

    Collection<FilmDto> searchFilms(FilmSearchDto search);

    Collection<FilmDto> getMostPopularFilms(int count, Integer genreId, Integer year);

    Collection<FilmDto> getCommonFilms(long userId, long friendId);
}