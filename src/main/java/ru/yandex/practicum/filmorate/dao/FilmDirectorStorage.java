package ru.yandex.practicum.filmorate.dao;

public interface FilmDirectorStorage {
    void add(long filmId, long directorId);
}
