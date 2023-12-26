package ru.yandex.practicum.filmorate.dao.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Repository
@Qualifier("FilmDbStorage")
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmDao {

    private final JdbcTemplate jdbcTemplate;

    private final FilmGenreDao filmGenreDao;

    private final FilmLikeDao filmLikeDao;

    @Override
    public Film add(final Film film) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO film (title, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey(), "Не удалось добавить фильм.").longValue());

        for (Genre genre : film.getGenres()) {
            filmGenreDao.add(film.getId(), genre.getId());
        }

        return film;
    }

    @Override
    public void remove(long id) {

    }

    @Override
    public void update(final Film film) {
        final String sql = "UPDATE film SET title = ?, description = ?, release_date = ?, duration = ?, mpa_id =? WHERE id = ?";
        int update = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        if (update != 1) {
            log.error("Фильм с id '{}' не найден.", film.getId());
            throw new NotFoundException("Фильм с id '" + film.getId() + "' не найден.");
        }

        for (Genre genre : film.getGenres()) {
            filmGenreDao.update(film.getId(), genre.getId());
        }
    }

    @Override
    public Collection<Film> findAll() {
        final String sql = "SELECT id, title, description, release_date, duration, mpa_id FROM film";
        final List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);

        for (Film film : films) {
            film.getLikes().addAll(filmLikeDao.getAllById(film.getId()));
            film.getGenres().addAll(filmGenreDao.getAllById(film.getId()));
        }

        return films;
    }

    @Override
    public Film findById(long id) {
        return null;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return new Film(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                Mpa.fromId(rs.getInt("mpa_id"))
        );
    }
}
