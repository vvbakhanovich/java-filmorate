package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.dto.FilmSearchDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmMark;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FilmStorage extends Dao<Film> {

    void addMarkToFilm(long filmId, long userId, Integer rating);

    void removeMarkFromFilm(long filmId, long userId);

    Map<Long, Set<FilmMark>> findUserIdFilmMarks();

    Collection<Film> findFilmsByIds(Set<Long> filmIds);

    Collection<Film> searchFilms(FilmSearchDto search);

    Collection<Film> findFilmsFromDirectorOrderBy(long directorId, String sortBy);

    Collection<Film> findMostLikedFilms(int count, Integer genreId, Integer year);

    Collection<Film> findCommonFilms(long userId, long friendId);
}
