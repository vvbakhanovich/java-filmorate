package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmLikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilmLikeDbStorage implements FilmLikeStorage {

    private static final Long NO_LIKES = 0L;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(final long filmId, final long userId) {
        final String sql = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public Long getCountById(long filmId) {
        try {
            final String sql = "SELECT COUNT(*) AS likes FROM film_like GROUP BY film_id HAVING film_id = ?";
            return jdbcTemplate.queryForObject(sql, Long.class, filmId);
        } catch (EmptyResultDataAccessException e) {
            log.debug("getCountById вернул пустую строку.");
        }
        return NO_LIKES;
    }

    @Override
    public Map<Long, Long> findAll() {
        final String sql = "SELECT film_id, COUNT(*) AS likes FROM film_like GROUP BY film_id";
        return jdbcTemplate.query(sql, this::mapRowToIdCount);
    }

    @Override
    public void remove(long filmId, long userId) {
        final String sql = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private Map<Long, Long> mapRowToIdCount(ResultSet rs) throws SQLException {
        final Map<Long, Long> result = new LinkedHashMap<>();
        while (rs.next()) {
            result.put(rs.getLong("film_id"), rs.getLong("likes"));
        }
        return result;
    }
}
