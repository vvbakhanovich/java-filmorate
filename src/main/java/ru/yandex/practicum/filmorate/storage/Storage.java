package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface Storage<T> {
    T add(T t);

    void remove(long id);

    void update(T t);

    Collection<T> findAll();

    T findById(long id);
}