package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.impl.DirectorDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    private DirectorStorage directorStorage;
    private Director director1;
    private Director director2;

    @BeforeAll
    public void init() {
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        director1 = Director.builder()
                .id(1)
                .name("Director 1")
                .build();
        director2 = Director.builder()
                .id(2)
                .name("Director 2")
                .build();
    }

    @Test
    @DisplayName("Получение списка режиссеров при пустой БД.")
    public void testFindEmptyDirectors() {
        Collection<Director> emptyDirectors = directorStorage.findAll();

        assertThat(emptyDirectors)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Получение режиссера с несуществующим id.")
    public void findDirectorUnknownId() {

        final long unknownId = 1;
        NotFoundException e = assertThrows(NotFoundException.class, () -> directorStorage.findById(unknownId));

        assertEquals("Режиссер с id '" + unknownId + "' не найден.", e.getMessage());
    }

    @Test
    @DisplayName("Добавление режиссера и поиск по id.")
    public void testAddAndFindById() {
        directorStorage.add(director1);
        Director storedDirector = directorStorage.findById(director1.getId());

        assertThat(storedDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(director1);
    }

    @Test
    @DisplayName("Добавление второго режиссера и поиск всех режиссеров.")
    public void testAddAndFindAll() {
        directorStorage.add(director1);
        directorStorage.add(director2);
        Collection<Director> directors = directorStorage.findAll();

        assertThat(directors)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(director1, director2));
    }

    @Test
    @DisplayName("Обновление данных режиссера.")
    public void testUpdate() {
        directorStorage.add(director1);

        director1.setName("Updated director 1");
        directorStorage.update(director1);
        Director updatedDirector = directorStorage.findById(director1.getId());

        assertThat(updatedDirector)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(director1);
    }

    @Test
    @DisplayName("Удаление режиссера с неизвестным id.")
    public void testDeleteUnknownDirectorId() {
        final long unknownId = 1;

        NotFoundException e = assertThrows(NotFoundException.class, () -> directorStorage.remove(unknownId));

        assertEquals("Режиссер с id '" + unknownId + "' не найден.", e.getMessage());
    }

    @Test
    @DisplayName("Удаление единственного режиссера.")
    public void testDeleteOnlyDirector() {
        directorStorage.add(director1);
        directorStorage.remove(director1.getId());
        NotFoundException e = assertThrows(NotFoundException.class, () -> directorStorage.findById(director1.getId()));

        assertEquals("Режиссер с id '" + director1.getId() + "' не найден.", e.getMessage());
    }

    @Test
    @DisplayName("Удаление режиссера из списка.")
    public void testDeleteDirector() {
        directorStorage.add(director1);
        directorStorage.add(director2);
        directorStorage.remove(director1.getId());
        Collection<Director> directors = directorStorage.findAll();

        assertThat(directors)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(director2));
    }
}