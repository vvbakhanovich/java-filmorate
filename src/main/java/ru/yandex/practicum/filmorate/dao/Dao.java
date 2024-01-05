package ru.yandex.practicum.filmorate.dao;

import java.util.Collection;

public interface Dao<T> {
    T add(T t);

    void remove(long id);

    void update(T t);

    Collection<T> findAll();

    T findById(long id);
}