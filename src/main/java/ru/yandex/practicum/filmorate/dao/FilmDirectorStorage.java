package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmDirectorStorage {
    void add(long filmId, long directorId);

    void batchUpdate(long filmId, Set<Director> directors);

    void deleteAllByFilmId(long filmId);

    Map<Long, List<Director>> findDirectorsInIdList(Set<Long> filmIds);

    List<Long> findFilmsByDirectorId(long directorId);

    List<Director> findAllById(long filmId);
}
