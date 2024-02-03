package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage extends Dao<Film> {
    Collection<Film> findMostLikedFilmsLimitBy(int count);

    Collection<Film> findMostLikedFilmsByGenre(int genre);

    Collection<Film> findFilmsByIds(Set<Long> filmIds);

    Collection<Film> findFilmsFromDirectorOrderBy(long directorId, String sortBy);
}
