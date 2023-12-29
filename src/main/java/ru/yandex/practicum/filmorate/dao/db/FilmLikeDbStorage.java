package ru.yandex.practicum.filmorate.dao.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FilmLikeDbStorage implements FilmLikeDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(final long filmId, final long userId) {
        final String sql = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void update(final long filmId, final long userId) {
        final String sql = "MERGE INTO film_like (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public Long getCountById(long filmId) {
        final String sql = "SELECT COUNT(*) AS likes FROM film_like GROUP BY film_id HAVING film_id = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, filmId);
    }

    @Override
    public Map<Long, Long> findAll() {
        final String sql = "SELECT film_id, COUNT(*) AS likes FROM film_like GROUP BY film_id";
        return jdbcTemplate.query(sql, this::mapRowToIdCount);
    }

    private Map<Long, Long> mapRowToIdCount(ResultSet rs) throws SQLException {
        final Map<Long, Long> result = new LinkedHashMap<>();
        while (rs.next()) {
            result.put(rs.getLong("film_id"), rs.getLong("likes"));
        }
        return result;
    }
}
