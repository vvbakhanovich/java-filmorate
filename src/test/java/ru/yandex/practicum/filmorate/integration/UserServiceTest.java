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
        user1 = User.builder()
                .id(1)
                .email("email 1")
                .login("login 1")
                .name("name 1")
                .birthday(LocalDate.now())
                .build();
        user2 = User.builder()
                .id(2)
                .email("email 2")
                .login("login 2")
                .name("name 2")
                .birthday(LocalDate.now())
                .build();
        user3 = User.builder()
                .id(3)
                .email("email 3")
                .login("login 3")
                .name("name 3")
                .birthday(LocalDate.now())
                .build();

        Mpa mpa = new Mpa(1, "G");
        film1 = Film.builder()
                .id(1)
                .name("film 1")
                .description("film 1 description")
                .releaseDate(LocalDate.of(2020, 12, 12))
                .duration(123)
                .mpa(mpa)
                .build();
        film2 = Film.builder()
                .id(2)
                .name("film 2")
                .description("film 2 description")
                .releaseDate(LocalDate.of(2020, 12, 12))
                .duration(123)
                .mpa(mpa)
                .build();
        film3 = Film.builder()
                .id(3)
                .name("film 3")
                .description("film 3 description")
                .releaseDate(LocalDate.of(2020, 12, 12))
                .duration(123)
                .mpa(mpa)
                .build();
        film4 = Film.builder()
                .id(4)
                .name("film 4")
                .description("film description 4")
                .releaseDate(LocalDate.of(2020, 12, 12))
                .duration(123)
                .mpa(mpa)
                .build();
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
        filmStorage.addLikeToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addLikeToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addLikeToFilm(film1.getId(), user2.getId(), 8);
        filmStorage.addLikeToFilm(film2.getId(), user2.getId(), 5);
        filmStorage.addLikeToFilm(film4.getId(), user2.getId(), 9);

        film4.setRating(9);

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
        filmStorage.addLikeToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addLikeToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addLikeToFilm(film1.getId(), user2.getId(), 8);
        filmStorage.addLikeToFilm(film2.getId(), user2.getId(), 6);
        filmStorage.addLikeToFilm(film4.getId(), user2.getId(), 9);

        film2.setRating(6);
        film4.setRating(9);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(FilmMapper.toDto(film2), FilmMapper.toDto(film4)));
    }

    @Test
    @DisplayName("Тест получения рекомендаций c тремя пользователями")
    public void findRecommendations() {
        filmStorage.addLikeToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addLikeToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addLikeToFilm(film1.getId(), user2.getId(), 8);
        filmStorage.addLikeToFilm(film2.getId(), user2.getId(), 5);

        filmStorage.addLikeToFilm(film1.getId(), user3.getId(), 9);
        filmStorage.addLikeToFilm(film2.getId(), user3.getId(), 6);
        filmStorage.addLikeToFilm(film3.getId(), user3.getId(), 7);
        filmStorage.addLikeToFilm(film4.getId(), user3.getId(), 9);
        film4.setRating(9);

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
        filmStorage.addLikeToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addLikeToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addLikeToFilm(film1.getId(), user2.getId(), 8);
        filmStorage.addLikeToFilm(film3.getId(), user2.getId(), 7);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест получения рекомендаций c тремя пользователями при одинаковых оценках, но с разным количеством лайков")
    public void findRecommendationsWithSameRatings() {
        filmStorage.addLikeToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addLikeToFilm(film3.getId(), user1.getId(), 7);

        filmStorage.addLikeToFilm(film1.getId(), user2.getId(), 8);
        filmStorage.addLikeToFilm(film3.getId(), user2.getId(), 7);

        filmStorage.addLikeToFilm(film1.getId(), user3.getId(), 8);
        filmStorage.addLikeToFilm(film2.getId(), user3.getId(), 5);
        filmStorage.addLikeToFilm(film3.getId(), user3.getId(), 7);
        filmStorage.addLikeToFilm(film4.getId(), user3.getId(), 9);
        film4.setRating(9);

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
        filmStorage.addLikeToFilm(film1.getId(), user1.getId(), 8);
        filmStorage.addLikeToFilm(film3.getId(), user1.getId(), 7);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isEmpty();
    }
}
