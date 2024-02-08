package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorStorage extends Dao<Director> {

    void addDirectorToFilm(long filmId, long directorId);
}
