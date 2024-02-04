package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDirectorDbStorage implements FilmDirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(final long filmId, final long directorId) {
        final String sql = "INSERT INTO film_director VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, directorId);
    }

    @Override
    public void batchUpdate(final long filmId, final Set<Director> directors) {
        final List<Director> directorList = new ArrayList<>(directors);
        final String sql = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, directorList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });
    }

    @Override
    public void deleteAllByFilmId(final long filmId) {
        final String sql = "DELETE FROM film_director WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public Map<Long, List<Director>> findDirectorsInIdList(final Set<Long> filmIds) {
        final String ids = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        final String sql = String.format(
                "SELECT fd.film_id, fd.director_id, d.director_name FROM film_director fd JOIN director d ON fd.director_id = d.id" +
                        " WHERE fd.film_id IN (%s)", ids);

        return jdbcTemplate.query(sql, this::extractToMap, filmIds.toArray());
    }

    @Override
    public List<Long> findFilmsByDirectorId(final long directorId) {
        final String sql = "SELECT film_id FROM film_director WHERE director_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, directorId);
    }

    @Override
    public List<Director> findAllById(long filmId) {
        final String sql = "SELECT fd.film_id, fd.director_id, d.director_name FROM film_director fd JOIN director d ON fd.director_id = d.id" +
                " WHERE fd.film_id = ?";
        return jdbcTemplate.query(sql, this::mapToDirector, filmId);
    }

    private Director mapToDirector(ResultSet rs, int i) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }

    private Map<Long, List<Director>> extractToMap(ResultSet rs) throws SQLException, DataAccessException {
        final Map<Long, List<Director>> filmIdDirectorMap = new HashMap<>();
        while (rs.next()) {
            final Long filmId = rs.getLong(1);
            List<Director> directors = filmIdDirectorMap.get(filmId);
            if (directors == null) {
                directors = new ArrayList<>();
            }
            final Director director = Director.builder()
                    .id(rs.getLong("director_id"))
                    .name(rs.getString("director_name"))
                    .build();
            directors.add(director);
            filmIdDirectorMap.put(filmId, directors);
        }
        return filmIdDirectorMap;
    }
}
