package ru.yandex.practicum.filmorate.dao.impl.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(final long filmId, final long genreId) {
        final String sql = "MERGE INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public void update(final long filmId, final long genreId) {
        deleteAll(filmId);
        final String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public List<Genre> findAllById(long filmId) {
        final String sql = "SELECT fg.genre_id, g.genre_name FROM film_genre fg JOIN genre g ON fg.genre_id = g.id" +
                " WHERE film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToLong, filmId);
    }

    @Override
    public void deleteAll(long id) {
        final String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void delete(long filmId, long genreId) {
        final String sql = "DELETE FROM film_genre WHERE film_id = ? AND genre_id = ?";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    private Genre mapRowToLong(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }
}
