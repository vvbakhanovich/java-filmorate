package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;

//TODO проверить генерацию айди для разных хранилок!!!
@Component
public class LongIdGenerator implements IdGenerator<Long> {

    long id = 1;

    @Override
    public Long generateId() {
        return id++;
    }
}