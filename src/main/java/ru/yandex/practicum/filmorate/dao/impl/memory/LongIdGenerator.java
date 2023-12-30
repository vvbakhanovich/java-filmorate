package ru.yandex.practicum.filmorate.dao.impl.memory;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.IdGenerator;

@Component
@Scope("prototype")
public class LongIdGenerator implements IdGenerator<Long> {

    private long id = 1;

    @Override
    public Long generateId() {
        return id++;
    }
}