package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EventStorage;


@Repository
@RequiredArgsConstructor
@Slf4j
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addEvent(String eventType, String operation, Long entityId, Long userId) {
        final String sqlFeedUpdate = "INSERT INTO feed_events (event_type, operation, entity_id, user_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlFeedUpdate, eventType, operation, entityId, userId);
    }
}
