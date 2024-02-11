package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.dto.FilmSearchDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface FilmStorage extends Dao<Film> {

    void addLikeToFilm(long filmId, long userId, int rating);

    void removeLikeFromFilm(long filmId, long userId);

    Map<Long, Map<Long, Integer>> getUsersAndFilmLikes();

    Collection<Film> findFilmsByIds(Set<Long> filmIds);

    Collection<Film> searchFilms(FilmSearchDto search);

    Collection<Film> findFilmsFromDirectorOrderBy(long directorId, String sortBy);

    Collection<Film> findMostLikedFilms(int count, Integer genreId, Integer year);

    Collection<Film> findCommonFilms(long userId, long friendId);
}
