package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Qualifier("UserDbStorage")
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(final User user) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO filmorate_user (email, login, nickname, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey(), "Не удалось добавить пользователя").longValue());
        return user;
    }

    @Override
    public void remove(final long id) {

    }

    @Override
    public void update(final User user) {

    }

    @Override
    public Collection<User> findAll() {
        final String allUserSql = "SELECT id, email, login, nickname, birthday FROM filmorate_user";

        List<User> users = jdbcTemplate.query(allUserSql, this::mapRowToUser);

        for (User user : users) {
            Map<Long, String> friends = getFriendList(user.getId());
            user.getFriends().putAll(friends);
        }
        return users;
    }

    @Override
    public User findById(final long id) {
        return null;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("nickname"),
                rs.getDate("birthday").toLocalDate()
        );
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

    private Map<Long, String> getFriendList(final long userId) {
        final String friendsSql = "SELECT f.friend_id, fs.status_name FROM friendship f LEFT JOIN friendship_status fs " +
                "ON f.friendship_status_id = fs.id WHERE f.user_id = ?";
        Map<Long, String> friends = jdbcTemplate.query(friendsSql, this::extractToFriendStatusMap, userId);
        if (friends == null) {
            friends = Collections.emptyMap();
        }
        return friends;
    }
}
