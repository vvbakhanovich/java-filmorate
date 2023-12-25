package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Repository
@Qualifier("UserDbStorage")
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        return null;
    }

    @Override
    public void remove(long id) {

    }

    @Override
    public void update(User user) {

    }

    @Override
    public Collection<User> findAll() {
        return null;
    }

    @Override
    public User findById(long id) {
        return null;
    }
}
