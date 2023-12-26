package ru.yandex.practicum.filmorate.dao.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static ru.yandex.practicum.filmorate.model.Friendship.NOT_ACK;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(long userId, long friendId) {
        final String sql = "INSERT INTO friendship (user_id, friend_id, friendship_status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, NOT_ACK.getStatusId());
    }

    @Override
    public void remove(long userId, long friendId) {
        final String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        int rows =  jdbcTemplate.update(sql, userId, friendId);
        if (rows != 1) {
            log.info("Удаление из друзей не было произведено, так как {} не был в списке друзей у {}", friendId, userId);
        }
    }

    @Override
    public Map<Long, Friendship> findById(final long userId) {
        final String friendsSql = "SELECT f.friend_id, fs.status_name FROM friendship f LEFT JOIN friendship_status fs " +
                "ON f.friendship_status_id = fs.id WHERE f.user_id = ?";
        Map<Long, Friendship> friends = jdbcTemplate.query(friendsSql, this::extractToFriendStatusMap, userId);
        if (friends == null) {
            friends = Collections.emptyMap();
        }
        return friends;
    }

    private Map<Long, Friendship> extractToFriendStatusMap(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Friendship> result = new LinkedHashMap<>();
        while (rs.next()) {
            Long friendId = rs.getLong("friend_id");
            String friendshipStatus = rs.getString("status_name");
            result.put(friendId, Friendship.fromString(friendshipStatus));
        }
        return result;
    }
}
