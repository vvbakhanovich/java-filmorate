package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.MpaStatus;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    private FilmDbStorage filmDbStorage;
    private Film film;
    Film updatedFilm;

    @BeforeEach
    public void setUp() {
        FilmGenreStorage filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate);
        FilmLikeStorage filmLikeStorage = new FilmLikeDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate, filmGenreStorage, filmLikeStorage);
        film = new Film(1L, "film", "film description", LocalDate.of(2020, 12, 12),
                123, new Mpa(1, MpaStatus.fromId(1).getName()));
        updatedFilm = new Film(1L, "updated film", "updated film description",
                LocalDate.of(2020, 12, 12), 123,
                new Mpa(1, MpaStatus.fromId(1).getName()));
    }

    @Test
    public void testAddAndFindByFilmId() {
        filmDbStorage.add(film);

        Film savedFilm = filmDbStorage.findById(1L);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
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
    public void testFindAllEmptyDb() {
        assertThat(filmDbStorage.findAll())
                .isNotNull()
                .isEmpty();
    }


}
