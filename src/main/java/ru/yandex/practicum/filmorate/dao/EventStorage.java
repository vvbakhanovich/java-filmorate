package ru.yandex.practicum.filmorate.dao;


public interface EventStorage {
    void addEvent(String eventType, String operation, Long entityId, Long userId);
}
