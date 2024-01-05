package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dao.impl.GenreDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GenreDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private GenreStorage genreStorage;

    @BeforeEach
    public void setUp() {
        genreStorage = new GenreDbStorage(jdbcTemplate);
    }


    @Test
    @DisplayName("Тест получения жанра 'Комедия")
    void testFindByIdComedy() {
        Genre genre = new Genre(1, "Комедия");

        Genre savedGenre = genreStorage.findById(1);

        assertThat(savedGenre)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genre);
    }

    @Test
    @DisplayName("Тест получения жанра 'Драма")
    void testFindByIdDrama() {
        Genre genre = new Genre(2, "Драма");

        Genre savedGenre = genreStorage.findById(2);

        assertThat(savedGenre)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genre);
    }

    @Test
    @DisplayName("Тест получения жанра 'Мультфильм")
    void testFindByIdCartoon() {
        Genre genre = new Genre(3, "Мультфильм");

        Genre savedGenre = genreStorage.findById(3);

        assertThat(savedGenre)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genre);
    }

    @Test
    @DisplayName("Тест получения жанра 'Триллер")
    void testFindByIdThriller() {
        Genre genre = new Genre(4, "Триллер");

        Genre savedGenre = genreStorage.findById(4);

        assertThat(savedGenre)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genre);
    }

    @Test
    @DisplayName("Тест получения жанра 'Документальный")
    void testFindByIdDocumentary() {
        Genre genre = new Genre(5, "Документальный");

        Genre savedGenre = genreStorage.findById(5);

        assertThat(savedGenre)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genre);
    }

    @Test
    @DisplayName("Тест получения жанра 'Боевик")
    void testFindByIdAction() {
        Genre genre = new Genre(6, "Боевик");

        Genre savedGenre = genreStorage.findById(6);

        assertThat(savedGenre)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(genre);
    }

    @Test
    @DisplayName("Тест получения списка всех жанров")
    void testFindAll() {
        List<Genre> allGenres = List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик")
        );

        Collection<Genre> storedGenres = genreStorage.findAll();

        assertThat(storedGenres)
                .isNotNull()
                .isNotEmpty()
                .containsAll(allGenres);
    }

    @Test
    @DisplayName("Тест получения жанра с неверным id")
    void findByWrongId() {

        NotFoundException e = assertThrows(NotFoundException.class, () -> genreStorage.findById(99));
        assertEquals("Жанр c id '99' не найден.", e.getMessage());
    }

}