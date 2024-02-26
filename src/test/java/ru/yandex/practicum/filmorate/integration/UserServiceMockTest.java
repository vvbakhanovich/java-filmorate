package ru.yandex.practicum.filmorate.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.FriendshipStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmMark;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {

    @Mock
    private UserStorage userStorage;
    @Mock
    private FilmStorage filmStorage;
    @Mock
    private FriendshipStorage friendshipStorage;
    @Mock
    private EventStorage eventStorage;
    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private User user3;
    private Film film1;
    private Film film2;
    private Film film3;
    private Film film4;

    @BeforeEach
    public void beforeEach() {
        user1 = createUser(1);
        user2 = createUser(2);
        user3 = createUser(3);
        film1 = createFilm(1);
        film2 = createFilm(2);
        film3 = createFilm(3);
        film4 = createFilm(4);
    }

    @Test
    @DisplayName("Тест получения рекомендаций c двумя пользователями")
    public void findRecommendationsBetweenTwoUsers() {
        when(filmStorage.findUserIdFilmMarks()).thenReturn(Map.of(
                user1.getId(), Set.of(
                        new FilmMark(user1.getId(), film1.getId(), 8),
                        new FilmMark(user1.getId(), film3.getId(), 7)
                ),
                user2.getId(), Set.of(
                        new FilmMark(user2.getId(), film1.getId(), 8),
                        new FilmMark(user2.getId(), film2.getId(), 5),
                        new FilmMark(user2.getId(), film4.getId(), 9)
                ))
        );

        when(filmStorage.findFilmsByIds(Set.of(2L, 4L))).thenReturn(List.of(film2, film4));

        film2.setRating(5);
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
        when(filmStorage.findUserIdFilmMarks()).thenReturn(Map.of(
                user1.getId(), Set.of(
                        new FilmMark(user1.getId(), film1.getId(), 8),
                        new FilmMark(user1.getId(), film3.getId(), 7)
                ),
                user2.getId(), Set.of(
                        new FilmMark(user2.getId(), film1.getId(), 8),
                        new FilmMark(user2.getId(), film2.getId(), 6),
                        new FilmMark(user2.getId(), film4.getId(), 9)
                ))
        );

        when(filmStorage.findFilmsByIds(Set.of(2L, 4L))).thenReturn(List.of(film2, film4));

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
        when(filmStorage.findUserIdFilmMarks()).thenReturn(Map.of(
                user1.getId(), Set.of(
                        new FilmMark(user1.getId(), film1.getId(), 8),
                        new FilmMark(user1.getId(), film3.getId(), 7)
                ),
                user2.getId(), Set.of(
                        new FilmMark(user2.getId(), film1.getId(), 8),
                        new FilmMark(user2.getId(), film2.getId(), 5)
                ),
                user3.getId(), Set.of(
                        new FilmMark(user3.getId(), film1.getId(), 9),
                        new FilmMark(user3.getId(), film2.getId(), 6),
                        new FilmMark(user3.getId(), film3.getId(), 7),
                        new FilmMark(user3.getId(), film4.getId(), 9)

                ))
        );

        when(filmStorage.findFilmsByIds(Set.of(2L, 4L))).thenReturn(List.of(film2, film4));

        film2.setRating(5.5);
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
        when(filmStorage.findUserIdFilmMarks()).thenReturn(Map.of(
                user1.getId(), Set.of(
                        new FilmMark(user1.getId(), film1.getId(), 8),
                        new FilmMark(user1.getId(), film3.getId(), 7)
                ),
                user2.getId(), Set.of(
                        new FilmMark(user2.getId(), film1.getId(), 8),
                        new FilmMark(user2.getId(), film3.getId(), 7)
                ))
        );

        when(filmStorage.findFilmsByIds(Collections.emptySet())).thenReturn(Collections.emptySet());

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест получения рекомендаций c тремя пользователями при одинаковых оценках, но с разным количеством лайков")
    public void findRecommendationsWithSameRatings() {
        when(filmStorage.findUserIdFilmMarks()).thenReturn(Map.of(
                user1.getId(), Set.of(
                        new FilmMark(user1.getId(), film1.getId(), 8),
                        new FilmMark(user1.getId(), film3.getId(), 7)
                ),
                user2.getId(), Set.of(
                        new FilmMark(user2.getId(), film1.getId(), 8),
                        new FilmMark(user2.getId(), film2.getId(), 7)
                ),
                user3.getId(), Set.of(
                        new FilmMark(user3.getId(), film1.getId(), 9),
                        new FilmMark(user3.getId(), film2.getId(), 5),
                        new FilmMark(user3.getId(), film3.getId(), 7),
                        new FilmMark(user3.getId(), film4.getId(), 9)

                ))
        );

        when(filmStorage.findFilmsByIds(Set.of(2L, 4L))).thenReturn(List.of(film2, film4));

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
          when(filmStorage.findUserIdFilmMarks()).thenReturn(Map.of(
                user1.getId(), Set.of(
                        new FilmMark(user1.getId(), film1.getId(), 8),
                        new FilmMark(user1.getId(), film3.getId(), 7)
                ),
                user2.getId(), Set.of(
                        new FilmMark(user2.getId(), film1.getId(), 9),
                        new FilmMark(user2.getId(), film2.getId(), 5),
                        new FilmMark(user2.getId(), film3.getId(), 6),
                        new FilmMark(user2.getId(), film4.getId(), 9)
                ),
                user3.getId(), Set.of(
                        new FilmMark(user3.getId(), film1.getId(), 6),
                        new FilmMark(user3.getId(), film2.getId(), 5),
                        new FilmMark(user3.getId(), film3.getId(), 10)
                ))
          );

        when(filmStorage.findFilmsByIds(Set.of(2L, 4L))).thenReturn(List.of(film2, film4));

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
        when(filmStorage.findUserIdFilmMarks()).thenReturn(Map.of(
                user1.getId(), Set.of(
                        new FilmMark(user1.getId(), film1.getId(), 8),
                        new FilmMark(user1.getId(), film3.getId(), 7)
                ))
        );

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест получения рекомендаций, когда у рекомендуемых фильмов отрицательные оценки")
    public void findRecommendationsWithNegativeRatings() {
        when(filmStorage.findUserIdFilmMarks()).thenReturn(Map.of(
                user1.getId(), Set.of(
                        new FilmMark(user1.getId(), film1.getId(), 8),
                        new FilmMark(user1.getId(), film3.getId(), 7)
                ),
                user2.getId(), Set.of(
                        new FilmMark(user2.getId(), film1.getId(), 9),
                        new FilmMark(user2.getId(), film2.getId(), 5),
                        new FilmMark(user2.getId(), film3.getId(), 6),
                        new FilmMark(user2.getId(), film4.getId(), 2)
                ),
                user3.getId(), Set.of(
                        new FilmMark(user3.getId(), film1.getId(), 6),
                        new FilmMark(user3.getId(), film2.getId(), 5),
                        new FilmMark(user3.getId(), film3.getId(), 10)
                ))
        );

        when(filmStorage.findFilmsByIds(Set.of(2L, 4L))).thenReturn(List.of(film2, film4));

        film2.setRating(5.0);
        film4.setRating(2.0);

        Collection<FilmDto> recommendations = userService.showRecommendations(user1.getId());

        assertThat(recommendations)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест получения рекомендаций, когда у других пользователей все отрицательные оценки")
    public void findRecommendationsWithAllNegativeRatings() {
        when(filmStorage.findUserIdFilmMarks()).thenReturn(Map.of(
                user1.getId(), Set.of(
                        new FilmMark(user1.getId(), film1.getId(), 8),
                        new FilmMark(user1.getId(), film3.getId(), 7)
                ),
                user2.getId(), Set.of(
                        new FilmMark(user2.getId(), film1.getId(), 1),
                        new FilmMark(user2.getId(), film2.getId(), 3),
                        new FilmMark(user2.getId(), film3.getId(), 5),
                        new FilmMark(user2.getId(), film4.getId(), 2)
                ),
                user3.getId(), Set.of(
                        new FilmMark(user3.getId(), film1.getId(), 3),
                        new FilmMark(user3.getId(), film2.getId(), 4),
                        new FilmMark(user3.getId(), film3.getId(), 1)
                ))
        );

        when(filmStorage.findFilmsByIds(Set.of(2L, 4L))).thenReturn(List.of(film2, film4));

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
