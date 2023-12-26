package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
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
    private final FriendshipDao friendshipDao;

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
        final String userUpdateSql = "UPDATE filmorate_user SET email = ?, login = ?, nickname = ?, birthday = ? " +
                "WHERE id = ?";
        final int update = jdbcTemplate.update(userUpdateSql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        if (update == 1) {
            log.info("Обновлен пользователь с id '{}'", user.getId());
        } else {
            log.error("Пользователь с id '{}' не найден.", user.getId());
            throw new NotFoundException("Пользователь с id '" + user.getId() + "' не найден.");
        }
    }

    @Override
    public Collection<User> findAll() {
        final String allUserSql = "SELECT id, email, login, nickname, birthday FROM filmorate_user";

        List<User> users = jdbcTemplate.query(allUserSql, this::mapRowToUser);

        for (User user : users) {
            Map<Long, String> friends = friendshipDao.findFriendsById(user.getId());
            user.getFriends().putAll(friends);
        }
        return users;
    }

    @Override
    public User findById(final long id) {
        final String userSql = "SELECT id, email, login, nickname, birthday FROM filmorate_user WHERE id = ?";
        try {
            final User user = jdbcTemplate.queryForObject(userSql, this::mapRowToUser, id);
            Map<Long, String> friends = friendshipDao.findFriendsById(user.getId());
            user.getFriends().putAll(friends);
            return user;
        } catch (DataAccessException e) {
            throw new NotFoundException("Пользователь с id '" + id + "' не найден.");
        }
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
}
