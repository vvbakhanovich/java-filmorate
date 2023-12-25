package ru.yandex.practicum.filmorate.dao;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LongIdGenerator implements IdGenerator<Long> {

    private long id = 1;

    @Override
    public Long generateId() {
        return id++;
    }
}