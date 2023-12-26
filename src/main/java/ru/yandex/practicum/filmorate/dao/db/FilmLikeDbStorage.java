package ru.yandex.practicum.filmorate.dao.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
    public List<Long> getAllById(long filmId) {
        final String sql = "SELECT user_id FROM film_like WHERE film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToLong, filmId);
    }

    private Long mapRowToLong(ResultSet rs, int rowNum) throws SQLException {
        return rs.getLong("user_id");
    }
}
