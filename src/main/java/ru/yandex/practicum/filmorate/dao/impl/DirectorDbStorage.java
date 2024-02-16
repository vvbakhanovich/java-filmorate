package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Director add(final Director director) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO director (director_name) VALUES (?)";
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        director.setId(Objects.requireNonNull(keyHolder.getKey(), "Не удалось добавить директора.").longValue());

        return director;
    }

    @Override
    public void remove(final long id) {
        final String sql = "DELETE FROM director WHERE id = ?";
        final int update = jdbcTemplate.update(sql, id);
        if (update != 1) {
            throw new NotFoundException("Режиссер с id '" + id + "' не найден.");
        }
    }

    @Override
    public void update(final Director director) {
        final String sql = "UPDATE director SET director_name = ? WHERE id = ?";
        final int update = jdbcTemplate.update(sql, director.getName(), director.getId());
        if (update != 1) {
            throw new NotFoundException("Режиссер с id '" + director.getId() + "' не найден.");
        }
    }

    @Override
    public Collection<Director> findAll() {
        final String sql = "SELECT * FROM director";
        return jdbcTemplate.query(sql, this::mapToDirector);
    }

    @Override
    public Director findById(final long id) {
        final String sql = "SELECT * FROM director WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapToDirector, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Режиссер с id '" + id + "' не найден.");
        }
    }

    @Override
    public void addDirectorToFilm(final long filmId, final long directorId) {
        final String sql = "INSERT INTO film_director VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, directorId);
    }

    private Director mapToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("director_name"))
                .build();
    }

}
