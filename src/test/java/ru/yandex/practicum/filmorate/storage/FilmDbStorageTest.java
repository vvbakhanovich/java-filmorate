package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.impl.DirectorDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.FilmSearchDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private FilmStorage filmDbStorage;
    private DirectorStorage directorStorage;
    private UserStorage userStorage;

    private Film film;
    private Film film2;
    private Film updatedFilm;
    private User user;
    private User user2;
    private Director director;

    @BeforeEach
    public void setUp() {
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate);
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

        user = User.builder()
                .id(1)
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .build();

        user2 = User.builder()
                .id(2)
                .email("email 2")
                .login("login 2")
                .name("name 2")
                .birthday(LocalDate.now())
                .build();

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

        Set<Genre> genres = filmDbStorage.findById(1).getGenres();


        assertThat(genres)
                .isNotNull()
                .isEqualTo(Set.of(genre1, genre2));
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

        Set<Genre> genres = filmDbStorage.findById(1).getGenres();


        assertThat(genres)
                .isNotNull()
                .isEqualTo(Set.of(genre1, genre2));

        film.getGenres().remove(genre2);
        film.getGenres().add(genre3);

        filmDbStorage.update(film);

        Set<Genre> updatedGenres = filmDbStorage.findById(1).getGenres();


        assertThat(updatedGenres)
                .isNotNull()
                .isEqualTo(Set.of(genre1, genre3));
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

        filmDbStorage.addLikeToFilm(film.getId(), user.getId(), 10);

        Film storedFilm = filmDbStorage.findById(film.getId());

        double rating = storedFilm.getRating();

        assertEquals(10, rating);
    }

    @Test
    @DisplayName("Тест удаления лайка")
    void testAddAndRemoveLike() {
        filmDbStorage.add(film);
        userStorage.add(user);

        filmDbStorage.addLikeToFilm(film.getId(), user.getId(), 1);
        Film storedFilm = filmDbStorage.findById(film.getId());
        double rating = storedFilm.getRating();

        assertEquals(1, rating);

        filmDbStorage.removeLikeFromFilm(film.getId(), user.getId());
        Film updatedFilm = filmDbStorage.findById(film.getId());
        double updatedRating = updatedFilm.getRating();

        assertEquals(0, updatedRating);
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
        filmDbStorage.addLikeToFilm(film.getId(), user.getId(), 1);

        film.setRating(1);

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
        filmDbStorage.addLikeToFilm(film.getId(), user.getId(), 1);

        film.setRating(1);

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
    @DisplayName("Тест на получение самых популярных фильмов определенного жанра за указанный год")
    void testMostPopularFilmsWithSpecifiedGenreAndYear() {
        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(6, "Боевик");

        userStorage.add(user);
        film.getGenres().add(genre1);
        film.getGenres().add(genre2);
        film.setReleaseDate(LocalDate.of(1999, 1, 1));
        filmDbStorage.add(film);
        filmDbStorage.addLikeToFilm(film.getId(), user.getId(), 1);

        film.setRating(1);

        Collection<Film> popularFilms = filmDbStorage.findMostLikedFilms(10, 1, 1999);

        assertThat(popularFilms)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(List.of(film));
    }

    @Test
    @DisplayName("Тест на получение самых популярных фильмов за указанный год и без жанров")
    void testMostPopularFilmsWithSpecifiedYearWithoutGenre() {

        userStorage.add(user);
        film.setReleaseDate(LocalDate.of(1999, 1, 1));
        filmDbStorage.add(film);
        filmDbStorage.addLikeToFilm(film.getId(), user.getId(), 1);

        film.setRating(1);

        Collection<Film> popularFilms = filmDbStorage.findMostLikedFilms(10, null, 1999);

        assertThat(popularFilms)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(List.of(film));
    }

    @Test
    @DisplayName("Тест на получение самых популярных фильмов указанного жанра и без года")
    void testMostPopularFilmsWithSpecifiedGenreWithoutYear() {

        Genre genre1 = new Genre(1, "Комедия");
        Genre genre2 = new Genre(6, "Боевик");

        userStorage.add(user);
        film.getGenres().add(genre1);
        film.getGenres().add(genre2);

        filmDbStorage.add(film);
        filmDbStorage.addLikeToFilm(film.getId(), user.getId(), 1);

        film.setRating(1);

        Collection<Film> popularFilms = filmDbStorage.findMostLikedFilms(10, 1, null);

        assertThat(popularFilms)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(List.of(film));
    }

    @Test
    @DisplayName("Тест поиск фильма по названию")
    void testSearchFilmByTitle() {
        Film newFilm = filmDbStorage.add(film);
        FilmSearchDto query = FilmSearchDto.builder()
                .by(List.of(SearchBy.TITLE.toString()))
                .query(newFilm.getName())
                .build();
        Collection<Film> films = filmDbStorage.searchFilms(query);

        assertThat(films)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(List.of(film));
    }

    @Test
    @DisplayName("Тест поиск фильма по режиссеру")
    void testSearchFilmByDirector() {
        directorStorage.add(director);
        film.getDirectors().add(director);
        Film newFilm = filmDbStorage.add(film);
        FilmSearchDto query = FilmSearchDto.builder()
                .by(List.of(SearchBy.DIRECTOR.toString()))
                .query(director.getName())
                .build();
        Collection<Film> films = filmDbStorage.searchFilms(query);

        assertThat(films)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(newFilm));
    }

    @Test
    @DisplayName("Тест поиск фильма по частичному названию")
    void testSearchFilmByPartialTitle() {
        Film newFilm = filmDbStorage.add(updatedFilm);
        FilmSearchDto query = FilmSearchDto.builder()
                .by(List.of(SearchBy.TITLE.toString()))
                .query(newFilm.getName().substring(0, 3))
                .build();
        Collection<Film> films = filmDbStorage.searchFilms(query);

        assertThat(films)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(List.of(newFilm));
    }

    @Test
    @DisplayName("Тест поиск фильмов по частичному названию фильма и режиссера")
    void testSearchFilmBylTitleAndDirector() {
        Director newDirector = Director.builder()
                .id(1)
                .name("name")
                .build();
        directorStorage.add(newDirector);

        Film filmWithDir = Film.builder()
                .name("film")
                .description("film description")
                .releaseDate(LocalDate.of(2019, 12, 12))
                .duration(123)
                .mpa(new Mpa(1, "G"))
                .build();
        filmWithDir.getDirectors().add(newDirector);
        filmWithDir = filmDbStorage.add(filmWithDir);

        Film fimWithName = Film.builder()
                .name("name")
                .description("film description")
                .releaseDate(LocalDate.of(2019, 12, 12))
                .duration(123)
                .mpa(new Mpa(1, "G"))
                .build();
        fimWithName = filmDbStorage.add(fimWithName);

        FilmSearchDto query = FilmSearchDto.builder()
                .by(SearchBy.getStringValues())
                .query(fimWithName.getName().substring(0, 3))
                .build();
        Collection<Film> films = filmDbStorage.searchFilms(query);

        assertThat(films)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .usingRecursiveComparison()
                .isEqualTo(List.of(filmWithDir, fimWithName));
    }

    @Test
    @DisplayName("Тест на получение самых популярных фильмов без указания жанра и года")
    void testMostPopularFilmsWithoutSpecifiedGenreAndYear() {

        User user2 = new User(2, "email2", "login2", "name2", LocalDate.now());
        User user3 = new User(3, "email3", "login3", "name3", LocalDate.now());

        userStorage.add(user);
        userStorage.add(user2);
        userStorage.add(user3);

        filmDbStorage.add(film);
        filmDbStorage.add(film2);

        filmDbStorage.addLikeToFilm(film.getId(), user.getId(), 10);
        filmDbStorage.addLikeToFilm(film.getId(), user2.getId(), 10);
        filmDbStorage.addLikeToFilm(film.getId(), user3.getId(), 10);

        filmDbStorage.addLikeToFilm(film2.getId(), user.getId(), 10);
        filmDbStorage.addLikeToFilm(film2.getId(), user2.getId(), 10);

        film.setRating(10);
        film2.setRating(10);

        Collection<Film> popularFilms = filmDbStorage.findMostLikedFilms(2, null, null);

        assertThat(popularFilms)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(List.of(film, film2));
    }

    @Test
    @DisplayName("Тест на получение самых популярных фильмов без указания жанра и года (один фильм без лайков)")
    void testMostPopularFilmsWithoutSpecifiedGenreAndYearOneFilmWithoutLikes() {

        User user2 = new User(2, "email2", "login2", "name2", LocalDate.now());
        User user3 = new User(3, "email3", "login3", "name3", LocalDate.now());

        userStorage.add(user);
        userStorage.add(user2);
        userStorage.add(user3);

        filmDbStorage.add(film);
        filmDbStorage.add(film2);

        filmDbStorage.addLikeToFilm(film.getId(), user.getId(), 10);
        filmDbStorage.addLikeToFilm(film.getId(), user2.getId(), 10);
        filmDbStorage.addLikeToFilm(film.getId(), user3.getId(), 10);

        film.setRating(10);

        Collection<Film> popularFilms = filmDbStorage.findMostLikedFilms(2, null, null);

        assertThat(popularFilms)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(List.of(film, film2));
    }

    @Test
    @DisplayName("Тест получения фильмов режиссера c сортировкой по году.")
    public void findFilmsByDirectorSortByYear() {
        film.getDirectors().add(director);
        film2.getDirectors().add(director);
        directorStorage.add(director);
        filmDbStorage.add(film);
        filmDbStorage.add(film2);

        Collection<Film> films = filmDbStorage.findFilmsFromDirectorOrderBy(director.getId(),
                SortBy.YEAR.getSql());

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
        film.setRating(1);

        userStorage.add(user);
        directorStorage.add(director);
        filmDbStorage.add(film);
        filmDbStorage.add(film2);
        filmDbStorage.addLikeToFilm(1, 1);
        System.out.println(film.getId());
        System.out.println(film2.getId());


        Collection<Film> films = filmDbStorage.findFilmsFromDirectorOrderBy(director.getId(),
                SortBy.LIKES.getSql());

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
        Collection<Film> films = filmDbStorage.findFilmsFromDirectorOrderBy(director.getId(),
                SortBy.YEAR.getSql());

        assertThat(films)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест получения списка общих фильмов")
    public void findCommonFilms() {
        filmDbStorage.add(film);
        filmDbStorage.add(film2);
        userStorage.add(user);
        userStorage.add(user2);
        filmDbStorage.addLikeToFilm(film.getId(), user.getId(), 10);
        filmDbStorage.addLikeToFilm(film2.getId(), user.getId(), 10);
        filmDbStorage.addLikeToFilm(film.getId(), user2.getId(), 10);
        film.setRating(10);

        Collection<Film> commonFilms = filmDbStorage.findCommonFilms(user.getId(), user2.getId());

        assertThat(commonFilms)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(film));
    }

    @Test
    @DisplayName("Тест получения пустого списка общих фильмов")
    public void findEmptyCommonFilms() {
        filmDbStorage.add(film);
        filmDbStorage.add(film2);
        userStorage.add(user);
        userStorage.add(user2);
        filmDbStorage.addLikeToFilm(film.getId(), user.getId(), 10);
        filmDbStorage.addLikeToFilm(film2.getId(), user2.getId(), 10);

        Collection<Film> commonFilms = filmDbStorage.findCommonFilms(user.getId(), user2.getId());

        assertThat(commonFilms)
                .isNotNull()
                .isEmpty();
    }
}
