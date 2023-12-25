package ru.yandex.practicum.filmorate.dao;

public interface IdGenerator<T extends Number> {
    T generateId();
}