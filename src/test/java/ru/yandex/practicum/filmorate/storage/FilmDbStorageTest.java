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
import ru.yandex.practicum.filmorate.dao.impl.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.db.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.db.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.MpaStatus;

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
    private FilmDbStorage filmDbStorage;
    private Film film;
    private Film updatedFilm;
    private FilmGenreStorage filmGenreStorage;

    @BeforeEach
    public void setUp() {
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate);
        FilmLikeStorage filmLikeStorage = new FilmLikeDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate, filmGenreStorage, filmLikeStorage);

        film = new Film(1L, "film", "film description", LocalDate.of(2020, 12, 12),
                123, new Mpa(1, MpaStatus.fromId(1).getName()));
        updatedFilm = new Film(1L, "updated film", "updated film description",
                LocalDate.of(2020, 12, 12), 123,
                new Mpa(1, MpaStatus.fromId(1).getName()));
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
        System.out.println(filmDbStorage.findAll());
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

        film.setId(99);
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
}
