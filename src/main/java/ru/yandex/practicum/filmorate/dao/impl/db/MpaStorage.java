package ru.yandex.practicum.filmorate.dao.impl.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MpaStorage implements ru.yandex.practicum.filmorate.dao.MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa findById(final int mpaId) {
        try {
            final String sql = "SELECT id, rating_name FROM mpa WHERE id = ?";
            return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Mpa рейтинш c id '" + mpaId + "' не найден.");
        }
    }

    @Override
    public Collection<Mpa> findAll() {
        final String sql = "SELECT id, rating_name FROM mpa";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return new Mpa(rs.getInt("id"), rs.getString("rating_name"));
    }
}
