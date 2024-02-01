package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmLikeStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
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

    @Override
    public Map<Long, Set<Long>> usersAndFilmLikes() {
        String filmsIdsSql = "SELECT user_id, film_id FROM film_like";
        return jdbcTemplate.query(filmsIdsSql, this::extractToMap);
    }

    private Map<Long, Set<Long>> extractToMap(ResultSet rs) throws SQLException, DataAccessException {
        final Map<Long, Set<Long>> userFilmLikesMap = new HashMap<>();
        while (rs.next()) {
            final Long userId = rs.getLong("user_id");
            Set<Long> filmLikes = userFilmLikesMap.get(userId);
            if (filmLikes == null) {
                filmLikes = new HashSet<>();
            }
            filmLikes.add(rs.getLong("film_id"));
            userFilmLikesMap.put(userId, filmLikes);
        }
        return userFilmLikesMap;
    }
}
