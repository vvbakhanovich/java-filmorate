package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendshipStorage;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(long userId, long friendId, int statusId) {
        final String sql = "INSERT INTO friendship (user_id, friend_id, friendship_status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, statusId);
    }

    @Override
    public void remove(long userId, long friendId) {
        final String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        final int rows = jdbcTemplate.update(sql, userId, friendId);
        if (rows != 1) {
            log.info("Удаление из друзей не было произведено, так как {} не был в списке друзей у {}", friendId, userId);
        }
    }

    @Override
    public void update(long userId, long friendId, int statusId) {
        final String sql = "UPDATE friendship SET friendship_status_id = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, statusId, userId, friendId);
    }
}
