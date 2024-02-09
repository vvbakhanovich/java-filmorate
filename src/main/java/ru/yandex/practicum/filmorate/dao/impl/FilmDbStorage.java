package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dto.FilmSearchDto;
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

        batchUpdateGenres(film.getId(), film.getGenres());
        batchUpdateDirectors(film.getId(), film.getDirectors());

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

        deleteAllGenresByFilmId(film.getId());
        batchUpdateGenres(film.getId(), film.getGenres());
        deleteAllDirectorsByFilmId(film.getId());
        batchUpdateDirectors(film.getId(), film.getDirectors());
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
            List<Genre> genres = findAllGenresByFilmId(filmId);
            List<Director> directors = findAllDirectorsByFilmId(filmId);
            film.getGenres().addAll(genres);
            film.getDirectors().addAll(directors);
            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id '" + filmId + "' не найден.");
        }
    }

    @Override
    public Collection<Film> findMostLikedFilms(final int count, final Integer genreId, final Integer year) {
        final StringBuilder sql = new StringBuilder(
                "SELECT f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, CAST (AVG (fl.RATING) AS DECIMAL(3,1)) AS rating " +
                        "FROM FILM_GENRE " +
                        "RIGHT JOIN film f on FILM_GENRE.FILM_ID = f.ID " +
                        "LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                        "LEFT JOIN film_like fl on f.id = fl.film_id " +
                        "WHERE YEAR(f.RELEASE_DATE) = COALESCE(?, YEAR(f.RELEASE_DATE)) ");
        Collection<Film> films;
        if (genreId != null) {
            sql.append("AND GENRE_ID = ? GROUP BY f.ID ORDER BY COUNT(fl.USER_ID) DESC LIMIT ?");
            films = jdbcTemplate.query(sql.toString(), this::mapToFilm, year, genreId, count);
        } else {
            sql.append("GROUP BY f.ID ORDER BY COUNT(fl.USER_ID) DESC LIMIT ?");
            films = jdbcTemplate.query(sql.toString(), this::mapToFilm, year, count);
        }
        setGenresForFilms(films);
        setDirectorsForFilms(films);
        return films;
    }

    @Override
    public Collection<Film> findFilmsFromDirectorOrderBy(final long directorId, final String sortBy) {
        final List<Long> filmsByDirectorId = findFilmsByDirectorId(directorId);
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

    @Override
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
        setDirectorsForFilms(films);
        setGenresForFilms(films);
        return films;
    }

    @Override
    public Collection<Film> searchFilms(FilmSearchDto search) {
        StringBuilder sql = new StringBuilder("SELECT " +
                "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, CAST (AVG (fl.RATING) AS DECIMAL(3,1)) AS rating, d.DIRECTOR_NAME " +
                "FROM FILM f " +
                "LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "LEFT JOIN film_like fl on f.id = fl.film_id " +
                "left join FILM_DIRECTOR fd on f.id = fd.FILM_ID " +
                "left join DIRECTOR D on fd.DIRECTOR_ID = D.ID " +
                "where ");

        if (search.getBy().contains(String.valueOf(SearchBy.TITLE))) {
            sql.append("f.TITLE ilike '%")
                    .append(search.getQuery())
                    .append("%' ");
        }
        if (search.getBy().size() == 2) {
            sql.append("OR ");
        }
        if (search.getBy().contains(String.valueOf(SearchBy.DIRECTOR))) {
            sql.append("d.DIRECTOR_NAME ilike '%")
                    .append(search.getQuery())
                    .append("%' ");
        }
        sql.append("GROUP BY f.id ORDER BY COUNT(fl.USER_ID) DESC");
        Collection<Film> films = jdbcTemplate.query(sql.toString(), this::mapToFilm);
        setDirectorsForFilms(films);
        setGenresForFilms(films);
        return films;
    }

    @Override
    public Collection<Film> findCommonFilms(long userId, long friendId) {
        final String commonFilmsIdsSql = "SELECT fl1.film_id FROM film_like fl1, film_like fl2 " +
                "WHERE fl1.user_id = ? AND fl2.user_id = ? AND fl1.film_id = fl2.film_id";
        final String sql = String.format(
                "SELECT " +
                        "f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, CAST (AVG (fl.RATING) AS DECIMAL(3,1)) AS rating " +
                        "FROM " +
                        "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                        "LEFT JOIN film_like fl on f.id = fl.film_id " +
                        "GROUP BY f.id, m.rating_name " +
                        "HAVING f.id IN (%s) " +
                        "ORDER BY COUNT(fl.USER_ID) DESC", commonFilmsIdsSql);
        return jdbcTemplate.query(sql, this::mapToFilm, userId, friendId);
    }

    @Override
    public void addLikeToFilm(final long filmId, final long userId, final int rating) {
        final String sql = "INSERT INTO film_like (film_id, user_id, rating) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, filmId, userId, rating);
    }

    @Override
    public void removeLikeFromFilm(long filmId, long userId) {
        final String sql = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public Map<Long, Map<Long, Integer>> getUsersAndFilmLikes() {
        String filmsIdsSql = "SELECT user_id, film_id, rating FROM film_like";
        return jdbcTemplate.query(filmsIdsSql, this::extractToUserIdLikedFilmsIdsMap);
    }

    public Map<Long, Set<Film>> findAllFilmsLikedByUsers() {
        final String sql = "SELECT fl.USER_ID, f.ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID, m.RATING_NAME, fl.RATING " +
                "FROM " +
                "FILM f LEFT JOIN MPA m ON f.MPA_ID = m.ID " +
                "JOIN film_like fl on f.id = fl.film_id " +
                "GROUP BY f.id, m.rating_name, fl.user_id";
        return jdbcTemplate.query(sql, this::extractToUserFilmMap);
    }

    private void setGenresForFilms(Collection<Film> films) {
        Map<Long, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, identity()));
        Map<Long, List<Genre>> filmIdGenreMap = findGenresInIdList(filmMap.keySet());
        filmIdGenreMap.forEach((id, genres) -> filmMap.get(id).getGenres().addAll(genres));
    }

    private void setDirectorsForFilms(Collection<Film> films) {
        Map<Long, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, identity()));
        Map<Long, List<Director>> filmIdDirectorMap = findDirectorsInIdList(filmMap.keySet());
        filmIdDirectorMap.forEach((id, directors) -> filmMap.get(id).getDirectors().addAll(directors));
    }

    private void batchUpdateGenres(final long filmId, final Set<Genre> genres) {
        final List<Genre> genreList = new ArrayList<>(genres);
        final String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, genreList.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    private void batchUpdateDirectors(final long filmId, final Set<Director> directors) {
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

    private void deleteAllGenresByFilmId(final long filmId) {
        final String sql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private void deleteAllDirectorsByFilmId(final long filmId) {
        final String sql = "DELETE FROM film_director WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private Map<Long, List<Genre>> findGenresInIdList(Set<Long> filmIds) {
        final String ids = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        final String sql = String.format(
                "SELECT fg.film_id, fg.genre_id, g.genre_name FROM film_genre fg JOIN genre g ON fg.genre_id = g.id" +
                        " WHERE fg.film_id IN (%s)", ids);

        return jdbcTemplate.query(sql, this::extractToFilmIdGenreMap, filmIds.toArray());
    }

    private Map<Long, List<Director>> findDirectorsInIdList(final Set<Long> filmIds) {
        final String ids = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        final String sql = String.format(
                "SELECT fd.film_id, fd.director_id, d.director_name FROM film_director fd JOIN director d ON fd.director_id = d.id" +
                        " WHERE fd.film_id IN (%s)", ids);

        return jdbcTemplate.query(sql, this::extractToFilmIdDirectorMap, filmIds.toArray());
    }

    private List<Genre> findAllGenresByFilmId(final long filmId) {
        final String sql = "SELECT fg.genre_id, g.genre_name FROM film_genre fg JOIN genre g ON fg.genre_id = g.id" +
                " WHERE film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    private List<Director> findAllDirectorsByFilmId(long filmId) {
        final String sql = "SELECT fd.film_id, fd.director_id, d.director_name FROM film_director fd JOIN director d ON fd.director_id = d.id" +
                " WHERE fd.film_id = ?";
        return jdbcTemplate.query(sql, this::mapToDirector, filmId);
    }

    private List<Long> findFilmsByDirectorId(final long directorId) {
        final String sql = "SELECT film_id FROM film_director WHERE director_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, directorId);
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

    private Map<Long, List<Genre>> extractToFilmIdGenreMap(ResultSet rs) throws SQLException, DataAccessException {
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

    private Map<Long, List<Director>> extractToFilmIdDirectorMap(ResultSet rs) throws SQLException, DataAccessException {
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

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("genre_name"));
    }

    private Director mapToDirector(ResultSet rs, int i) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }

    private Map<Long, Map<Long, Integer>> extractToUserIdLikedFilmsIdsMap(ResultSet rs) throws SQLException, DataAccessException {
        final Map<Long, Map<Long, Integer>> userFilmLikesMap = new HashMap<>();
        while (rs.next()) {
            final Long userId = rs.getLong("user_id");
            Map<Long, Integer> filmLikes = userFilmLikesMap.get(userId);
            if (filmLikes == null) {
                filmLikes = new HashMap<>();
            }
            filmLikes.put(rs.getLong("film_id"), rs.getInt("rating"));
            userFilmLikesMap.put(userId, filmLikes);
        }
        return userFilmLikesMap;
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
