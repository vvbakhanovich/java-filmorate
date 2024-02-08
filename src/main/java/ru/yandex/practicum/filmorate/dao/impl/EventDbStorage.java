package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


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

    @Override
    public Collection<Feed> getFeed(long id) {
        final String sql = "SELECT * FROM FEED_EVENTS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, this::extractToFeedList, id);
    }

    private List<Feed> extractToFeedList(ResultSet rs) throws SQLException, DataAccessException {

        final List<Feed> feed = new ArrayList<>();

        while (rs.next()) {
            Feed currentFeed = Feed.builder()
                    .entityId(rs.getLong("entity_id"))
                    .eventType(EventType.valueOf(rs.getString("event_type")))
                    .eventId(rs.getLong("id"))
                    .operation(Operation.valueOf(rs.getString("operation")))
                    .userId(rs.getLong("user_id"))
                    .timestamp(rs.getTimestamp("publication_time").getTime())
                    .build();
            feed.add(currentFeed);
        }

        return feed;
    }
}
