package ru.yandex.practicum.filmorate.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.FriendshipStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dao.impl.EventDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.dao.impl.UserDbStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private UserService userService;
    private UserStorage userStorage;
    private FilmStorage filmStorage;

    private User user1;
    private User user2;
    private User user3;
    private Film film1;
    private Film film2;
    private Film film3;
    private Film film4;

    @BeforeAll
    public void beforeAll() {
        userStorage = new UserDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate);
        FriendshipStorage friendshipStorage = new FriendshipDbStorage(jdbcTemplate);
        EventStorage eventStorage = new EventDbStorage(jdbcTemplate);
        userService = new UserServiceImpl(userStorage, filmStorage, friendshipStorage, eventStorage);
    }

    @BeforeEach
    public void beforeEach() {
        user1 = createUser(1);
        user2 = createUser(2);
        user3 = createUser(3);
        film1 = createFilm(1);
        film2 = createFilm(2);
        film3 = createFilm(3);
        film4 = createFilm(4);
        userStorage.add(user1);
        userStorage.add(user2);
        userStorage.add(user3);
        filmStorage.add(film1);
        filmStorage.add(film2);
        filmStorage.add(film3);
        filmStorage.add(film4);
    }

    @Test
    @DisplayName("Тест получения рекомендаций c двумя пользователями")
    public void findRecommendationsBetweenTwoUsers() {
        filmStorage.addMarkToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addMarkToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addMarkToFilm(film1.getId(), user2.getId(), 8);
        filmStorage.addMarkToFilm(film2.getId(), user2.getId(), 5);
        filmStorage.addMarkToFilm(film4.getId(), user2.getId(), 9);

        film4.setRating(9.0);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(FilmMapper.toDto(film4)));
    }

    @Test
    @DisplayName("Тест получения нескольких рекомендаций c двумя пользователями")
    public void findSeveralRecommendationsBetweenTwoUsers() {
        filmStorage.addMarkToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addMarkToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addMarkToFilm(film1.getId(), user2.getId(), 8);
        filmStorage.addMarkToFilm(film2.getId(), user2.getId(), 6);
        filmStorage.addMarkToFilm(film4.getId(), user2.getId(), 9);

        film2.setRating(6.0);
        film4.setRating(9.0);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(FilmMapper.toDto(film2), FilmMapper.toDto(film4)));
    }

    @Test
    @DisplayName("Тест получения рекомендаций с разным количеством совпавших оцененных фильмов")
    public void findRecommendations() {
        filmStorage.addMarkToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addMarkToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addMarkToFilm(film1.getId(), user2.getId(), 8);
        filmStorage.addMarkToFilm(film2.getId(), user2.getId(), 5);

        filmStorage.addMarkToFilm(film1.getId(), user3.getId(), 9);
        filmStorage.addMarkToFilm(film2.getId(), user3.getId(), 6);
        filmStorage.addMarkToFilm(film3.getId(), user3.getId(), 7);
        filmStorage.addMarkToFilm(film4.getId(), user3.getId(), 9);
        film4.setRating(9.0);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(FilmMapper.toDto(film4)));
    }

    @Test
    @DisplayName("Тест получение пустого списка рекомендаций при совпадении оценок")
    public void getEmptyListWhenSameRatings() {
        filmStorage.addMarkToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addMarkToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addMarkToFilm(film1.getId(), user2.getId(), 8);
        filmStorage.addMarkToFilm(film3.getId(), user2.getId(), 7);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест получения рекомендаций c тремя пользователями при одинаковых оценках, но с разным количеством лайков")
    public void findRecommendationsWithSameRatings() {
        filmStorage.addMarkToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addMarkToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addMarkToFilm(film1.getId(), user2.getId(), 8);
        filmStorage.addMarkToFilm(film3.getId(), user2.getId(), 7);

        filmStorage.addMarkToFilm(film1.getId(), user3.getId(), 8);
        filmStorage.addMarkToFilm(film2.getId(), user3.getId(), 5);
        filmStorage.addMarkToFilm(film3.getId(), user3.getId(), 7);
        filmStorage.addMarkToFilm(film4.getId(), user3.getId(), 9);
        film4.setRating(9.0);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(FilmMapper.toDto(film4)));
    }

    @Test
    @DisplayName("Тест получения рекомендаций c разницей в несколько баллов")
    public void findRecommendationsWithDifferentRatings() {
        filmStorage.addMarkToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addMarkToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addMarkToFilm(film1.getId(), user2.getId(), 9);
        filmStorage.addMarkToFilm(film2.getId(), user2.getId(), 5);
        filmStorage.addMarkToFilm(film3.getId(), user2.getId(), 6);
        filmStorage.addMarkToFilm(film4.getId(), user2.getId(), 9);

        filmStorage.addMarkToFilm(film1.getId(), user3.getId(), 6);
        filmStorage.addMarkToFilm(film2.getId(), user3.getId(), 5);
        filmStorage.addMarkToFilm(film3.getId(), user3.getId(), 10);
        film4.setRating(9.0);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(FilmMapper.toDto(film4)));
    }

    @Test
    @DisplayName("Тест получение пустого списка рекомендаций, когда только один пользователь")
    public void getEmptyListWithOnlyOneUser() {
        filmStorage.addMarkToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addMarkToFilm(film3.getId(), user1.getId(), 7);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест получения рекомендаций, когда у рекомендуемых фильмов отрицательные оценки")
    public void findRecommendationsWithNegativeRatings() {
        filmStorage.addMarkToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addMarkToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addMarkToFilm(film1.getId(), user2.getId(), 9);
        filmStorage.addMarkToFilm(film2.getId(), user2.getId(), 5);
        filmStorage.addMarkToFilm(film3.getId(), user2.getId(), 6);
        filmStorage.addMarkToFilm(film4.getId(), user2.getId(), 2);

        filmStorage.addMarkToFilm(film1.getId(), user3.getId(), 6);
        filmStorage.addMarkToFilm(film2.getId(), user3.getId(), 5);
        filmStorage.addMarkToFilm(film3.getId(), user3.getId(), 10);
        film4.setRating(9.0);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест получения рекомендаций, когда у других пользователей все отрицательные оценки")
    public void findRecommendationsWithAllNegativeRatings() {
        filmStorage.addMarkToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addMarkToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addMarkToFilm(film1.getId(), user2.getId(), 1);
        filmStorage.addMarkToFilm(film2.getId(), user2.getId(), 3);
        filmStorage.addMarkToFilm(film3.getId(), user2.getId(), 5);
        filmStorage.addMarkToFilm(film4.getId(), user2.getId(), 2);

        filmStorage.addMarkToFilm(film1.getId(), user3.getId(), 3);
        filmStorage.addMarkToFilm(film2.getId(), user3.getId(), 4);
        filmStorage.addMarkToFilm(film3.getId(), user3.getId(), 1);
        film4.setRating(9.0);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isEmpty();
    }

    private User createUser(int id) {
        return User.builder()
                .id(id)
                .email("email " + id)
                .login("login " + id)
                .name("name " + id)
                .birthday(LocalDate.now())
                .build();
    }

    private Film createFilm(int id) {
        Mpa mpa = new Mpa(1, "G");
        return Film.builder()
                .id(id)
                .name("film " + id)
                .description("film " + id + " description")
                .releaseDate(LocalDate.of(2020, 12, 12))
                .duration(123)
                .mpa(mpa)
                .build();
    }
}
