package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {
    T add(T t);

    boolean remove(long id);

    boolean update(T t);

    List<T> findAl();
}