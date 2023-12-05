package ru.yandex.practicum.filmorate.storage;

public class LongIdGenerator implements IdGenerator<Long> {

    long id = 1;

    @Override
    public Long generateId() {
        return id++;
    }
}