package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmGenreStorage {
    void add(long filmId, long genreId);

    List<Genre> findAllById(long filmId);

    Map<Long, List<Genre>> findGenresInIdList(Set<Long> filmIds);

    void deleteAllById(long filmId);

    void batchUpdate(long filmId, List<Genre> genres);
}
