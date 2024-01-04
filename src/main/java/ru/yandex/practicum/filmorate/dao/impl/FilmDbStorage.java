package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private final FilmGenreStorage filmGenreStorage;

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

        filmGenreStorage.batchUpdate(film.getId(), film.getGenres());

        return film;
    }

    @Override
    public void remove(final long id) {
        final String sql = "DELETE FROM film WHERE id = ?";
        jdbcTemplate.update(sql, id);
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

        filmGenreStorage.deleteAllById(film.getId());

        filmGenreStorage.batchUpdate(film.getId(), film.getGenres());
    }

    @Override
    public Collection<Film> findAll() {
        final String sql = "SELECT " +
                "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, fg.GENRE_ID, g.GENRE_NAME, COUNT(fl.USER_ID) AS likes " +
                "FROM " +
                "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID " +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID " +
                "LEFT JOIN film_like fl on f.id = fl.film_id " +
                "GROUP BY f.id, m.rating_name, fg.genre_id, g.genre_name";

        return jdbcTemplate.query(sql, this::extractToFilmList);

    }

    @Override
    public Film findById(final long filmId) {
        final String sql = "SELECT " +
                "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, fg.GENRE_ID, g.GENRE_NAME, COUNT(fl.USER_ID) AS likes " +
                "FROM " +
                "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID " +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID " +
                "LEFT JOIN film_like fl on f.id = fl.film_id " +
                "GROUP BY f.id, m.rating_name, fg.genre_id, g.genre_name " +
                "HAVING f.ID = ?";

        final Film film = jdbcTemplate.query(sql, this::extractToFilm, filmId);

        if (film == null) {
            throw new NotFoundException("Фильм с id '" + filmId + "' не найден.");
        }
        return film;
    }

    public Collection<Film> findMostLikedFilmsLimitBy(final int count) {
        final String sql = "SELECT " +
                "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, fg.GENRE_ID, g.GENRE_NAME, COUNT(fl.USER_ID) AS likes " +
                "FROM " +
                "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID " +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID " +
                "LEFT JOIN film_like fl on f.id = fl.film_id " +
                "GROUP BY f.id, m.rating_name, fg.genre_id, g.genre_name " +
                "ORDER BY COUNT(fl.USER_ID) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::extractToFilmList, count);
    }

    private Film extractToFilm(ResultSet rs) throws SQLException, DataAccessException {

        Film film = null;
        final Map<Long, Film> filmIdMap = new HashMap<>();

        while (rs.next()) {

            Long filmId = rs.getLong(1);
            film = filmIdMap.get(filmId);
            if (film == null) {
                film = new Film(
                        filmId,
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration"),
                        new Mpa(rs.getInt("mpa_id"), rs.getString("rating_name"))
                );
                film.setLikes(rs.getLong("likes"));
                filmIdMap.put(filmId, film);
            }

            final int genre_id = rs.getInt("genre_id");
            if (genre_id == 0) {
                film.getGenres().addAll(Collections.emptyList());
                continue;
            }

            final Genre genre = new Genre();
            genre.setId(genre_id);
            genre.setName(rs.getString("genre_name"));
            film.getGenres().add(genre);
        }

        return film;
    }

    private Collection<Film> extractToFilmList(ResultSet rs) throws SQLException, DataAccessException {

        final Map<Long, Film> filmIdMap = new LinkedHashMap<>();

        while (rs.next()) {

            Long filmId = rs.getLong(1);
            Film film = filmIdMap.get(filmId);
            if (film == null) {
                film = new Film(
                        filmId,
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("release_date").toLocalDate(),
                        rs.getInt("duration"),
                        new Mpa(rs.getInt("mpa_id"), rs.getString("rating_name"))
                );
                film.setLikes(rs.getLong("likes"));
                filmIdMap.put(filmId, film);
            }

            final int genre_id = rs.getInt("genre_id");
            if (genre_id == 0) {
                film.getGenres().addAll(Collections.emptyList());
                continue;
            }

            final Genre genre = new Genre();
            genre.setId(genre_id);
            genre.setName(rs.getString("genre_name"));
            film.getGenres().add(genre);
        }

        return filmIdMap.values();
    }
}
