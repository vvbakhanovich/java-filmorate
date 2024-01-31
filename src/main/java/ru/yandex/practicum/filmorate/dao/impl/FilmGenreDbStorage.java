package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(final long filmId, final long genreId) {
        final String sql = "MERGE INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    @Override
    public List<Genre> findAllById(final long filmId) {
        final String sql = "SELECT fg.genre_id, g.genre_name FROM film_genre fg JOIN genre g ON fg.genre_id = g.id" +
                " WHERE film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToLong, filmId);
    }

    @Override
    public Map<Long, List<Genre>> findGenresInIdList(Set<Long> filmIds) {
        final String ids = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        final String sql = String.format(
                "SELECT fg.film_id, fg.genre_id, g.genre_name FROM film_genre fg JOIN genre g ON fg.genre_id = g.id" +
                        " WHERE fg.film_id IN (%s)", ids);

        return jdbcTemplate.query(sql, this::extractToMap, filmIds.toArray());
    }

    @Override
    public void deleteAllById(final long filmId) {
        final String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public void batchUpdate(final long filmId, final List<Genre> genres) {
        final String sql = "MERGE INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    private Genre mapRowToLong(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }

    private Map<Long, List<Genre>> extractToMap(ResultSet rs) throws SQLException, DataAccessException {
        final Map<Long, List<Genre>> filmIdGenreMap = new HashMap<>();
        while (rs.next()) {
            final Long filmId = rs.getLong(1);
            List<Genre> genres = filmIdGenreMap.get(filmId);
            if (genres == null) {
                genres = new ArrayList<>();
            }
            final Genre genre = new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
            genres.add(genre);
            filmIdGenreMap.put(filmId, genres);
        }
        return filmIdGenreMap;
    }
}
