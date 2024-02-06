package ru.yandex.practicum.filmorate.dao;


import ru.yandex.practicum.filmorate.model.Feed;

import java.util.Collection;

public interface EventStorage {
    void addEvent(String eventType, String operation, Long entityId, Long userId);

    Collection<Feed> getFeed(long id);
}
