package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        return null;
    }

    @Override
    public void remove(long id) {

    }

    @Override
    public void update(Film film) {

    }

    @Override
    public Collection<Film> findAll() {
        return null;
    }

    @Override
    public Film findById(long id) {
        return null;
    }
}
