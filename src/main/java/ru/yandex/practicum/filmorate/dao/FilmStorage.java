package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.dto.FilmSearchDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage extends Dao<Film> {

    Collection<Film> findFilmsByIds(Set<Long> filmIds);

    Collection<Film> searchFilms(FilmSearchDto search);

    Collection<Film> findFilmsFromDirectorOrderBy(long directorId, String sortBy);

    Collection<Film> findMostLikedFilms(int count, Integer genreId, Integer year);

    Collection<Film> findFilmsByIdsOrderByLikes(Set<Long> filmIds);
}
