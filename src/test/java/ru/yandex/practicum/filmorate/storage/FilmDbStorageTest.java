package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.dao.FilmLikeStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmDbStorage;
    private FilmGenreStorage filmGenreStorage;
    private FilmLikeStorage filmLikeStorage;
    private UserStorage userStorage;

    private Film film;
    private Film updatedFilm;
    private User user;

    @BeforeEach
    public void setUp() {
        filmLikeStorage = new FilmLikeDbStorage(jdbcTemplate);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate, filmGenreStorage);
        userStorage = new UserDbStorage(jdbcTemplate);

        Mpa mpa = new Mpa(1, "G");

        film = Film.builder()
                .id(1)
                .name("film")
                .description("film description")
                .releaseDate(LocalDate.of(2020, 12, 12))
                .duration(123)
                .mpa(mpa)
                .build();

        updatedFilm = Film.builder()
                .id(1)
                .name("updated film")
                .description("updated film description")
                .releaseDate(LocalDate.of(2020, 12, 12))
                .duration(123)
                .mpa(mpa)
                .build();

        user = new User(1, "email", "login", "name", LocalDate.now());
    }

    @Test
    @DisplayName("Тест добавления и получения по id")
    public void testAddAndFindByFilmId() {
        filmDbStorage.add(film);

        Film savedFilm = filmDbStorage.findById(1L);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    @DisplayName("Тест получения фильма с несуществующим id")
    public void testFindByWrongId() {

        NotFoundException e = assertThrows(NotFoundException.class, () -> filmDbStorage.findById(99));

        assertEquals("Фильм с id '99' не найден.", e.getMessage());
    }

    @Test
    @DisplayName("Тест обновления данных фильма")
    public void testUpdate() {

        filmDbStorage.add(film);
        filmDbStorage.update(updatedFilm);

        Film savedFilm = filmDbStorage.findById(1L);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedFilm);
    }

    @Test
    @DisplayName("Тест обновления данных фильма с несуществующим id")
    public void testUpdateWithWrongId() {

        filmDbStorage.add(film);
        updatedFilm.setId(99);

        NotFoundException e = assertThrows(NotFoundException.class, () -> filmDbStorage.update(updatedFilm));

        assertEquals("Фильм с id '99' не найден.", e.getMessage());
    }

    @Test
    @DisplayName("Тест получения списка всех фильмов")
    public void testFindAll() {
        filmDbStorage.add(film);
        filmDbStorage.add(updatedFilm);

        Collection<Film> films = filmDbStorage.findAll();

        updatedFilm.setId(2L);

        assertThat(films)
                .isNotNull()
                .isNotEmpty()
                .containsAll(List.of(film, updatedFilm));
    }

    @Test
    @DisplayName("Тест получения списка всех фильмов при пустой таблице")
    public void testFindAllEmptyDb() {
        assertThat(filmDbStorage.findAll())
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест на добавление и получение списка жанров по id фильма")
    void testAddAndGetById() {
        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(6, "Боевик");
        film.getGenres().add(genre1);
        film.getGenres().add(genre2);
        filmDbStorage.add(film);

        List<Genre> genres = filmGenreStorage.findAllById(1);


        assertThat(genres)
                .isNotNull()
                .isEqualTo(List.of(genre1, genre2));
    }

    @Test
    @DisplayName("Тест на обновление и получение списка жанров по id фильма")
    void testUpdateAndGetById() {
        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(6, "Боевик");
        Genre genre3 = new Genre(2, "Драма");

        film.getGenres().add(genre1);
        film.getGenres().add(genre2);
        filmDbStorage.add(film);

        List<Genre> genres = filmGenreStorage.findAllById(1);


        assertThat(genres)
                .isNotNull()
                .isEqualTo(List.of(genre1, genre2));

        film.getGenres().remove(genre2);
        film.getGenres().add(genre3);

        filmDbStorage.update(film);

        List<Genre> updatedGenres = filmGenreStorage.findAllById(1);


        assertThat(updatedGenres)
                .isNotNull()
                .isEqualTo(List.of(genre1, genre3));
    }

    @Test
    @DisplayName("Тест удаления фильма из БД")
    void testDeleteById() {
        filmDbStorage.add(film);
        filmDbStorage.remove(film.getId());

        assertThat(filmDbStorage.findAll())
                .isNotNull()
                .isEmpty();

        NotFoundException e = assertThrows(NotFoundException.class, () -> filmDbStorage.findById(film.getId()));

        assertEquals("Фильм с id '1' не найден.", e.getMessage());
    }

    @Test
    @DisplayName("Тест добавления лайка")
    void testAddLike() {
        filmDbStorage.add(film);
        userStorage.add(user);

        filmLikeStorage.add(film.getId(), user.getId());

        Film storedFilm = filmDbStorage.findById(film.getId());

        long likes = storedFilm.getLikes();

        assertEquals(1, likes);
    }

    @Test
    @DisplayName("Тест удаления лайка")
    void testAddAndRemoveLike() {
        filmDbStorage.add(film);
        userStorage.add(user);

        filmLikeStorage.add(film.getId(), user.getId());
        Film storedFilm = filmDbStorage.findById(film.getId());
        long likes = storedFilm.getLikes();

        assertEquals(1, likes);

        filmLikeStorage.remove(film.getId(), user.getId());
        Film updatedFilm = filmDbStorage.findById(film.getId());
        long updatedLikes = updatedFilm.getLikes();

        assertEquals(0, updatedLikes);
    }

    @Test
    @DisplayName("Тест поиска всех фильмов со всеми полями")
    void testFindAllWithAllFields() {
        userStorage.add(user);

        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(6, "Боевик");

        film.getGenres().add(genre1);
        film.getGenres().add(genre2);
        filmDbStorage.add(film);
        filmLikeStorage.add(film.getId(), user.getId());

        film.setLikes(1);

        assertThat(filmDbStorage.findAll())
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(List.of(film));
    }

    @Test
    @DisplayName("Тест поиска по id фильма со всеми полями")
    void testFindByIdWithAllFields() {
        userStorage.add(user);

        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(6, "Боевик");

        film.getGenres().add(genre1);
        film.getGenres().add(genre2);
        filmDbStorage.add(film);
        filmLikeStorage.add(film.getId(), user.getId());

        film.setLikes(1);

        assertThat(filmDbStorage.findById(film.getId()))
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    @DisplayName("Тест удаление фильма")
    void testDeleteFilm() {
        Film newFilm = filmDbStorage.add(film);
        filmDbStorage.remove(newFilm.getId());

        String formattedResponse = String.format("Фильм с id '%s' не найден.", newFilm.getId());
        NotFoundException e = assertThrows(NotFoundException.class, () -> filmDbStorage.findById(newFilm.getId()));
        assertEquals(formattedResponse, e.getMessage());
    }

    @Test
    @DisplayName("Тест удаление несуществующего фильма")
    void testDeleteNotExistingUser() {
        int filmId = 999;
        String formattedResponse = String.format("Фильм с id '%s' не найден.", filmId);
        NotFoundException e = assertThrows(NotFoundException.class, () -> filmDbStorage.findById(filmId));
        assertEquals(formattedResponse, e.getMessage());
    }

    @Test
    @DisplayName("Тест удаление всех лайков с фильма")
    void testDeleteAllLikesFromFilm() {
        Film newFilm = filmDbStorage.add(film);
        User newUser = userStorage.add(user);

        filmLikeStorage.add(newFilm.getId(), newUser.getId());
        filmLikeStorage.removeAllLikesByFilmId(newFilm.getId());
        Film updatedFilm = filmDbStorage.findById(newFilm.getId());
        long updatedLikes = updatedFilm.getLikes();
        assertEquals(0, updatedLikes);
    }
}
