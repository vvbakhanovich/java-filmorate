package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.stream.Collectors;

import static java.util.function.Function.identity;

@Repository
@RequiredArgsConstructor
@Slf4j
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
        int amount = jdbcTemplate.update(sql, id);
        if (amount != 1) {
            throw new NotFoundException("Фильм с id '" + id + "' не найден.");
        }
    }

    @Override
    public void update(final Film film) {
        final String sql = "UPDATE film SET title = ?, description = ?, release_date = ?, duration = ?, mpa_id =? WHERE id = ?";
        int update = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        if (update != 1) {
            throw new NotFoundException("Фильм с id '" + film.getId() + "' не найден.");
        }

        filmGenreStorage.deleteAllById(film.getId());
        filmGenreStorage.batchUpdate(film.getId(), film.getGenres());
    }

    @Override
    public Collection<Film> findAll() {
        final String sql = "SELECT " +
                "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, COUNT(fl.USER_ID) AS likes " +
                "FROM " +
                "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "LEFT JOIN film_like fl on f.id = fl.film_id " +
                "GROUP BY f.id, m.rating_name";

        Collection<Film> films = jdbcTemplate.query(sql, this::mapToFilm);
        return setGenresForFilms(films);
    }

    @Override
    public Film findById(final long filmId) {
        final String sql = "SELECT " +
                "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, COUNT(fl.USER_ID) AS likes " +
                "FROM " +
                "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "LEFT JOIN film_like fl on f.id = fl.film_id " +
                "GROUP BY f.id, m.rating_name " +
                "HAVING f.ID = ?";

        try {
            final Film film = jdbcTemplate.queryForObject(sql, this::mapToFilm, filmId);
            List<Genre> genres = filmGenreStorage.findAllById(filmId);
            film.getGenres().addAll(genres);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id '" + filmId + "' не найден.");
        }
    }

    public Collection<Film> findMostLikedFilmsLimitBy(final int count) {
        final String sql = "SELECT " +
                "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, COUNT(fl.USER_ID) AS likes " +
                "FROM " +
                "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "LEFT JOIN film_like fl on f.id = fl.film_id " +
                "GROUP BY f.id, m.rating_name " +
                "ORDER BY COUNT(fl.USER_ID) DESC " +
                "LIMIT ?";

        Collection<Film> films = jdbcTemplate.query(sql, this::mapToFilm, count);
        return setGenresForFilms(films);
    }

    private List<Film> setGenresForFilms(Collection<Film> films) {
        Map<Long, Film> filmMap = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        Map<Long, List<Genre>> filmIdGenreMap = filmGenreStorage.findGenresInIdList(filmMap.keySet());
        filmIdGenreMap.forEach((id, genres) -> filmMap.get(id).getGenres().addAll(genres));
        return new ArrayList<>(filmMap.values());
    }

    private Film mapToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong(1))
                .name(rs.getString("title"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("rating_name")))
                .build();
        film.setLikes(rs.getLong("likes"));
        return film;
    }
}
