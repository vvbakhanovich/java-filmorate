package ru.yandex.practicum.filmorate.storage;

public interface Storage<T> {
    long add(T t);

    boolean remove(long id);

    boolean update(T t);
}