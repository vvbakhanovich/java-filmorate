package ru.yandex.practicum.filmorate.dao.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.FilmLikeDao;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

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
        final String sql = "SELECT f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, " +
                "fg.GENRE_ID, g.GENRE_NAME FROM FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID LEFT JOIN FILM_GENRE fg ON " +
                "f.ID = fg.FILM_ID LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID";
        final Map<Long, Film> idFilmMap = jdbcTemplate.query(sql, this::extractToFilmList);
        final Map<Long, Long> filmLikesMap = filmLikeDao.findAll();

        if (!filmLikesMap.isEmpty()) {
            for (Long filmId : filmLikesMap.keySet()) {
                Film film = idFilmMap.get(filmId);
                film.setLikes(filmLikesMap.get(filmId));
            }
        }
        return idFilmMap.values();
    }

    @Override
    public Film findById(long id) {
        final String sql = "SELECT f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, " +
                "fg.GENRE_ID, g.GENRE_NAME FROM FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID LEFT JOIN FILM_GENRE fg ON " +
                "f.ID = fg.FILM_ID LEFT JOIN GENRE g ON fg.GENRE_ID = g.ID WHERE f.id = ?";

        final Film film = jdbcTemplate.query(sql, this::extractToFilm, id);
        if (film != null) {
            film.setLikes(filmLikeDao.getCountById(id));
        } else {
            throw new NotFoundException("Фильм с id '" + id + "'не найден.");
        }
        return film;
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

    private Map<Long, Film> extractToFilmList(ResultSet rs) throws SQLException, DataAccessException {

        final Map<Long, Film> filmIdMap = new HashMap<>();

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

        return filmIdMap;
    }
}
