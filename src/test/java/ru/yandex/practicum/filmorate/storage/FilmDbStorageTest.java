package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dao.impl.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

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
    private DirectorStorage directorStorage;
    private UserStorage userStorage;

    private Film film;
    private Film film2;
    private Film updatedFilm;
    private User user;
    private Director director;

    @BeforeEach
    public void setUp() {
        filmLikeStorage = new FilmLikeDbStorage(jdbcTemplate);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        FilmDirectorStorage filmDirectorStorage = new FilmDirectorDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate, filmGenreStorage, filmDirectorStorage);
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

        film2 = Film.builder()
                .id(2)
                .name("film")
                .description("film description")
                .releaseDate(LocalDate.of(2019, 12, 12))
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

        director = Director.builder()
                .id(1)
                .name("Director")
                .build();
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
        NotFoundException e = assertThrows(NotFoundException.class, () -> filmDbStorage.remove(filmId));
        assertEquals(formattedResponse, e.getMessage());
    }

    @Test
    @DisplayName("Тест на получение самых популярных фильмов с несколькими жанрами")
    void testMostPopularFilmsWithSeveralGenres() {
        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(6, "Боевик");
        Genre genre3 = new Genre(2, "Драма");

        userStorage.add(user);
        film.getGenres().add(genre1);
        film.getGenres().add(genre2);
        film.getGenres().add(genre3);
        filmDbStorage.add(film);
        filmLikeStorage.add(film.getId(), user.getId());
        filmDbStorage.add(updatedFilm);

        film.setLikes(1);

        Collection<Film> popularFilms = filmDbStorage.findMostLikedFilmsLimitBy(1);

        assertThat(popularFilms)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(List.of(film));
    }

    @Test
    @DisplayName("Тест получения фильмов режиссера c сортировкой по году.")
    public void findFilmsByDirectorSortByYear() {
        film.getDirectors().add(director);
        film2.getDirectors().add(director);
        directorStorage.add(director);
        filmDbStorage.add(film);
        filmDbStorage.add(film2);

        Collection<Film> films = filmDbStorage.findFilmsFromDirectorOrderByYear(director.getId());

        assertThat(films)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film2, film));
    }

    @Test
    @DisplayName("Тест получения фильмов режиссера c сортировкой по лайкам.")
    public void findFilmsByDirectorSortByLikes() {
        film.getDirectors().add(director);
        film2.getDirectors().add(director);
        film.setLikes(1);

        userStorage.add(user);
        directorStorage.add(director);
        filmDbStorage.add(film);
        filmDbStorage.add(film2);
        filmLikeStorage.add(1, 1);
        System.out.println(film.getId());
        System.out.println(film2.getId());


        Collection<Film> films = filmDbStorage.findFilmsFromDirectorOrderByLikes(director.getId());

        assertThat(films)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film, film2));
    }

    @Test
    @DisplayName("Тест получения пустого списка, когда у режиссера нет фильмов.")
    public void findFilmsByDirectorUnknownId() {
        directorStorage.add(director);
        Collection<Film> films = filmDbStorage.findFilmsFromDirectorOrderByYear(director.getId());

        assertThat(films)
                .isNotNull()
                .isEmpty();
    }
}
