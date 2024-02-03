package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

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

    private final FilmDirectorStorage filmDirectorStorage;

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
        filmDirectorStorage.batchUpdate(film.getId(), film.getDirectors());

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
        filmDirectorStorage.deleteAllByFilmId(film.getId());
        filmDirectorStorage.batchUpdate(film.getId(), film.getDirectors());
    }

    @Override
    public Collection<Film> findAll() {
        final String sql = "SELECT " +
                "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, CAST (AVG (fl.RATING) AS DECIMAL(3,1)) AS rating " +
                "FROM " +
                "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "LEFT JOIN film_like fl on f.id = fl.film_id " +
                "GROUP BY f.id, m.rating_name";

        Collection<Film> films = jdbcTemplate.query(sql, this::mapToFilm);
        setGenresForFilms(films);
        setDirectorsForFilms(films);
        return films;
    }

    @Override
    public Film findById(final long filmId) {
        final String sql = "SELECT " +
                "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, CAST (AVG (fl.RATING) AS DECIMAL(3,1)) AS rating " +
                "FROM " +
                "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "LEFT JOIN film_like fl on f.id = fl.film_id " +
                "GROUP BY f.id, m.rating_name " +
                "HAVING f.ID = ?";

        try {
            final Film film = jdbcTemplate.queryForObject(sql, this::mapToFilm, filmId);
            List<Genre> genres = filmGenreStorage.findAllById(filmId);
            List<Director> directors = filmDirectorStorage.findAllById(filmId);
            film.getGenres().addAll(genres);
            film.getDirectors().addAll(directors);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id '" + filmId + "' не найден.");
        }
    }

    public Collection<Film> findMostLikedFilmsLimitBy(final int count) {
        final String sql = "SELECT " +
                "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, CAST (AVG (fl.RATING) AS DECIMAL(3,1)) AS rating " +
                "FROM " +
                "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "LEFT JOIN film_like fl on f.id = fl.film_id " +
                "GROUP BY f.id, m.rating_name " +
                "ORDER BY rating DESC " +
                "LIMIT ?";

        Collection<Film> films = jdbcTemplate.query(sql, this::mapToFilm, count);
        setGenresForFilms(films);
        setDirectorsForFilms(films);

        return films;
    }

    @Override
    public Collection<Film> findFilmsFromDirectorOrderBy(final long directorId, final String sortBy) {
        final List<Long> filmsByDirectorId = filmDirectorStorage.findFilmsByDirectorId(directorId);
        final String ids = String.join(",", Collections.nCopies(filmsByDirectorId.size(), "?"));
        final String sql = String.format(
                "SELECT " +
                        "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, CAST (AVG (fl.RATING) AS DECIMAL(3,1)) AS rating " +
                        "FROM " +
                        "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                        "LEFT JOIN film_like fl on f.id = fl.film_id " +
                        "GROUP BY f.id, m.rating_name " +
                        "HAVING f.id IN (%s) " +
                        "ORDER BY ", ids);
        final StringBuilder sb = new StringBuilder();
        String sqlWithSort = sb.append(sql).append(sortBy).toString();
        final List<Film> directorFilms = jdbcTemplate.query(sqlWithSort, this::mapToFilm, filmsByDirectorId.toArray());
        setGenresForFilms(directorFilms);
        setDirectorsForFilms(directorFilms);

        return directorFilms;
    }

    public Collection<Film> findFilmsByIds(Set<Long> filmIds) {
        final String ids = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        final String sql = String.format(
                "SELECT " +
                        "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, CAST (AVG (fl.RATING) AS DECIMAL(3,1)) AS rating " +
                        "FROM " +
                        "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                        "LEFT JOIN film_like fl on f.id = fl.film_id " +
                        "WHERE f.ID IN (%s)" +
                        "GROUP BY f.id, m.rating_name ", ids);

        Collection<Film> films = jdbcTemplate.query(sql, this::mapToFilm, filmIds.toArray());
        return setGenresForFilms(films);
    }

    public Map<Long, Set<Film>> findAllFilmsLikedByUsers() {
        final String sql = "SELECT fl.USER_ID, f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, fl.RATING " +
                "FROM " +
                "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "JOIN film_like fl on f.id = fl.film_id " +
                "GROUP BY f.id, m.rating_name, fl.user_id";
        return jdbcTemplate.query(sql, this::extractToUserFilmMap);
    }

    private List<Film> setGenresForFilms(Collection<Film> films) {
        Map<Long, Film> filmMap = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        Map<Long, List<Genre>> filmIdGenreMap = filmGenreStorage.findGenresInIdList(filmMap.keySet());
        filmIdGenreMap.forEach((id, genres) -> filmMap.get(id).getGenres().addAll(genres));
        return new ArrayList<>(filmMap.values());
    }

    private List<Film> setDirectorsForFilms(Collection<Film> films) {
        Map<Long, Film> filmMap = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        Map<Long, List<Director>> filmIdDirectorMap = filmDirectorStorage.findDirectorsInIdList(filmMap.keySet());
        filmIdDirectorMap.forEach((id, directors) -> filmMap.get(id).getDirectors().addAll(directors));
        return new ArrayList<>(filmMap.values());
    }

    private Film mapToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("title"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("rating_name")))
                .build();
        film.setRating(rs.getDouble("rating"));
        return film;
    }

    private Map<Long, Set<Film>> extractToUserFilmMap(ResultSet rs) throws SQLException, DataAccessException {
        final Map<Long, Set<Film>> userIdLikedFilmsMap = new HashMap<>();
        while (rs.next()) {
            Long userId = rs.getLong("user_id");
            Set<Film> films = userIdLikedFilmsMap.get(userId);
            if (films == null) {
                films = new HashSet<>();
            }
            Film film = mapToFilm(rs, rs.getRow());
            films.add(film);
            userIdLikedFilmsMap.put(userId, films);
        }
        return userIdLikedFilmsMap;
    }
}
