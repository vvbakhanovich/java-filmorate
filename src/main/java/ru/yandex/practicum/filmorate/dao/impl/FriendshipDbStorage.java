package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void merge(Friendship friendship) {
        final String sql = "MERGE INTO friendship (user_id, friend_id, friendship_status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, friendship.getUserId(), friendship.getFriendId(),
                friendship.getFriendshipStatus().getStatusId());
    }

    @Override
    public void remove(long userId, long friendId) {

    }

    @Override
    public Collection<Friendship> findAll() {
        return null;
    }

    @Override
    public Map<Long, String> findFriendsById(final long userId) {
        final String friendsSql = "SELECT f.friend_id, fs.status_name FROM friendship f LEFT JOIN friendship_status fs " +
                "ON f.friendship_status_id = fs.id WHERE f.user_id = ?";
        Map<Long, String> friends = jdbcTemplate.query(friendsSql, this::extractToFriendStatusMap, userId);
        if (friends == null) {
            friends = Collections.emptyMap();
        }
        return friends;
    }

    private Map<Long, String> extractToFriendStatusMap(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, String> result = new LinkedHashMap<>();
        while (rs.next()) {
            Long friendId = rs.getLong("friend_id");
            String friendshipStatus = rs.getString("status_name");
            result.put(friendId, friendshipStatus);
        }
        return result;
    }
}
