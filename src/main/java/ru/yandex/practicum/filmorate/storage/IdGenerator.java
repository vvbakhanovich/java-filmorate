package ru.yandex.practicum.filmorate.storage;

public interface IdGenerator<T extends Number> {
    T generateId();
}