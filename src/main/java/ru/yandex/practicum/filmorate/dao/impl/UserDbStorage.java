package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

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
        return null;
    }

    @Override
    public User findById(final long id) {
        return null;
    }
}
