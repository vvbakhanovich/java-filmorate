package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.MpaStorage;
import ru.yandex.practicum.filmorate.dao.impl.db.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MpaDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    private MpaStorage mpaStorage;

    @BeforeEach
    public void setUp() {
        mpaStorage = new MpaDbStorage(jdbcTemplate);
    }

    @Test
    @DisplayName("Тест получения по id рейтинга 'G'")
    void findByIdG() {
        final Mpa mpa = new Mpa(1, "G");
        final Mpa storedMpa = mpaStorage.findById(1);

        assertThat(storedMpa)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mpa);
    }

    @Test
    @DisplayName("Тест получения по id рейтинга 'PG'")
    void findByIdPG() {
        final Mpa mpa = new Mpa(2, "PG");
        final Mpa storedMpa = mpaStorage.findById(2);

        assertThat(storedMpa)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mpa);
    }

    @Test
    @DisplayName("Тест получения по id рейтинга 'PG-13'")
    void findByIdPG13() {
        final Mpa mpa = new Mpa(3, "PG-13");
        final Mpa storedMpa = mpaStorage.findById(3);

        assertThat(storedMpa)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mpa);
    }

    @Test
    @DisplayName("Тест получения по id рейтинга 'R'")
    void findByIdR() {
        final Mpa mpa = new Mpa(4, "R");
        final Mpa storedMpa = mpaStorage.findById(4);

        assertThat(storedMpa)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mpa);
    }

    @Test
    @DisplayName("Тест получения по id рейтинга 'NC-17'")
    void findByIdNC17() {
        final Mpa mpa = new Mpa(5, "NC-17");
        final Mpa storedMpa = mpaStorage.findById(5);

        assertThat(storedMpa)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(mpa);
    }

    @Test
    @DisplayName("Тест получения списка всех рейтингов Mpa")
    void findAll() {

        final List<Mpa> mpas = List.of(
                new Mpa(1, "G"),
                new Mpa(2, "PG"),
                new Mpa(3, "PG-13"),
                new Mpa(4, "R"),
                new Mpa(5, "NC-17")
        );
        final Collection<Mpa> storedMpas = mpaStorage.findAll();

        assertThat(storedMpas)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(mpas);
    }

    @Test
    @DisplayName("Тест получения рейтинга с неверным id")
    void findByWrongId() {

        NotFoundException e = assertThrows(NotFoundException.class, () -> mpaStorage.findById(99));
        assertEquals("Mpa рейтинг c id '99' не найден.", e.getMessage());
    }
}