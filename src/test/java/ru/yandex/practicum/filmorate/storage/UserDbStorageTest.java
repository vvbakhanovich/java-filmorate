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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.model.FriendshipStatus.ACKNOWLEDGED;
import static ru.yandex.practicum.filmorate.model.FriendshipStatus.NOT_ACKNOWLEDGED;


@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {

    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    private FriendshipStorage friendshipStorage;
    private FilmGenreStorage filmGenreStorage;
    private FilmLikeStorage filmLikeStorage;
    private FilmStorage filmDbStorage;
    private User user;
    private User updatedUser;
    private User anotherUser;


    @BeforeEach
    void setUp() {
        filmLikeStorage = new FilmLikeDbStorage(jdbcTemplate);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate, filmGenreStorage);
        userStorage = new UserDbStorage(jdbcTemplate, filmDbStorage);
        friendshipStorage = new FriendshipDbStorage(jdbcTemplate);
        user = User.builder()
                .id(1)
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .build();
        updatedUser = User.builder()
                .id(1)
                .email("new_email")
                .login("new_login")
                .name("new_name")
                .birthday(LocalDate.now())
                .build();
        anotherUser = User.builder()
                .id(2)
                .email("another_email")
                .login("another_login")
                .name("another_name")
                .birthday(LocalDate.now())
                .build();
    }

    @Test
    @DisplayName("Тест добавления и получения по id")
    public void testAddAndUserByFilmId() {
        userStorage.add(user);

        User savedUser = userStorage.findById(1L);

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(user);
    }

    @Test
    @DisplayName("Тест получения пользователя с несуществующим id")
    public void testFindByWrongId() {

        NotFoundException e = assertThrows(NotFoundException.class, () -> userStorage.findById(99));

        assertEquals("Пользователь с id '99' не найден.", e.getMessage());
    }

    @Test
    @DisplayName("Тест обновления данных пользователя")
    public void testUpdate() {

        userStorage.add(user);
        userStorage.update(updatedUser);

        User storedUser = userStorage.findById(1L);

        assertThat(storedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedUser);
    }

    @Test
    @DisplayName("Тест обновления данных пользователя с несуществующим id")
    public void testUpdateWithWrongId() {

        userStorage.add(user);
        updatedUser.setId(99);

        NotFoundException e = assertThrows(NotFoundException.class, () -> userStorage.update(updatedUser));

        assertEquals("Пользователь с id '99' не найден.", e.getMessage());
    }

    @Test
    @DisplayName("Тест получения списка всех пользователей")
    public void testFindAll() {
        userStorage.add(user);
        userStorage.add(anotherUser);

        Collection<User> users = userStorage.findAll();

        assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .containsAll(List.of(user, anotherUser));
    }

    @Test
    @DisplayName("Тест получения списка всех пользователей при пустой таблице")
    public void testFindAllEmptyDb() {
        assertThat(userStorage.findAll())
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест удаления пользователя из БД")
    void testDeleteById() {
        userStorage.add(user);
        userStorage.remove(user.getId());

        assertThat(userStorage.findAll())
                .isNotNull()
                .isEmpty();

        NotFoundException e = assertThrows(NotFoundException.class, () -> userStorage.findById(user.getId()));

        assertEquals("Пользователь с id '1' не найден.", e.getMessage());
    }

    @Test
    @DisplayName("Тест удаления пользователя с несуществующим из БД")
    void testDeleteByWrongId() {

        NotFoundException e = assertThrows(NotFoundException.class, () -> userStorage.remove(99));

        assertEquals("Пользователь с id '99' не найден.", e.getMessage());
    }

    @Test
    @DisplayName("Тест добавления пользователя в друзья")
    void testAddFriend() {
        userStorage.add(user);
        userStorage.add(anotherUser);

        friendshipStorage.add(1, 2, NOT_ACKNOWLEDGED.getId());
        User storedUser = userStorage.findById(user.getId());
        Friendship friendship = new Friendship(2L, NOT_ACKNOWLEDGED.getStatus());

        assertThat(storedUser.getFriends())
                .isNotNull()
                .isNotEmpty()
                .containsExactly(friendship);
    }

    @Test
    @DisplayName("Тест добавления нескольких пользователей в друзья")
    void testAddMultipleFriends() {
        userStorage.add(user);
        userStorage.add(anotherUser);
        updatedUser.setId(3);
        userStorage.add(updatedUser);

        friendshipStorage.add(1, 2, NOT_ACKNOWLEDGED.getId());
        friendshipStorage.add(1, 3, ACKNOWLEDGED.getId());
        User storedUser = userStorage.findById(user.getId());
        Friendship friendship1 = new Friendship(2L, NOT_ACKNOWLEDGED.getStatus());
        Friendship friendship2 = new Friendship(3L, ACKNOWLEDGED.getStatus());

        assertThat(storedUser.getFriends())
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(List.of(friendship1, friendship2));
    }

    @Test
    @DisplayName("Тест обновления статуса друга у пользователя")
    void testUpdateFriendshipStatus() {
        userStorage.add(user);
        userStorage.add(anotherUser);

        friendshipStorage.add(1, 2, NOT_ACKNOWLEDGED.getId());
        friendshipStorage.update(1, 2, ACKNOWLEDGED.getId());
        User storedUser = userStorage.findById(user.getId());
        Friendship friendship = new Friendship(2L, ACKNOWLEDGED.getStatus());

        assertThat(storedUser.getFriends())
                .isNotNull()
                .isNotEmpty()
                .containsExactly(friendship);
    }

    @Test
    @DisplayName("Тест получения списка всех пользователей с полем друзья")
    void testFindAllWithFriends() {
        userStorage.add(user);
        userStorage.add(anotherUser);

        friendshipStorage.add(1, 2, NOT_ACKNOWLEDGED.getId());
        Friendship friendship = new Friendship(2L, NOT_ACKNOWLEDGED.getStatus());
        user.getFriends().add(friendship);

        assertThat(userStorage.findAll())
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(List.of(user, anotherUser));
    }

    @Test
    @DisplayName("Тест удаления пользователей из друзей")
    void testRemoveSingleFriend() {
        userStorage.add(user);
        userStorage.add(anotherUser);
        updatedUser.setId(3);
        userStorage.add(updatedUser);

        friendshipStorage.add(1, 2, NOT_ACKNOWLEDGED.getId());
        friendshipStorage.add(1, 3, ACKNOWLEDGED.getId());

        friendshipStorage.remove(1, 2);
        User storedUser = userStorage.findById(user.getId());
        Friendship friendship2 = new Friendship(3L, ACKNOWLEDGED.getStatus());

        assertThat(storedUser.getFriends())
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(List.of(friendship2));
    }

    @Test
    @DisplayName("Тест удаления пользователей из друзей (1 друг)")
    void testRemoveFriend() {
        userStorage.add(user);
        userStorage.add(anotherUser);

        friendshipStorage.add(1, 2, NOT_ACKNOWLEDGED.getId());

        friendshipStorage.remove(1, 2);
        User storedUser = userStorage.findById(user.getId());

        assertThat(storedUser.getFriends())
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест получения списка друзей")
    void testGetFriendsList() {
        userStorage.add(user);
        userStorage.add(anotherUser);

        friendshipStorage.add(1, 2, NOT_ACKNOWLEDGED.getId());

        Collection<User> friends = userStorage.findFriendsByUserId(user.getId());

        assertThat(friends)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(List.of(anotherUser));
    }

    @Test
    @DisplayName("Тест получения списка друзей из нескольких пользователей")
    void testGetMultFriendsList() {
        userStorage.add(user);
        userStorage.add(anotherUser);
        updatedUser.setId(3L);
        userStorage.add(updatedUser);

        friendshipStorage.add(1, 2, NOT_ACKNOWLEDGED.getId());
        friendshipStorage.add(1, 3, ACKNOWLEDGED.getId());

        Collection<User> friends = userStorage.findFriendsByUserId(user.getId());

        assertThat(friends)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(List.of(anotherUser, updatedUser));
    }

    @Test
    @DisplayName("Тест получения пустого списка друзей")
    void testGetEmptyFriendsList() {
        userStorage.add(user);

        Collection<User> friends = userStorage.findFriendsByUserId(user.getId());

        assertThat(friends)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест получения списка рекомендаций фильмов")
    void testGetRecommendationsList() {
        userStorage.add(user);
        userStorage.add(anotherUser);

        Mpa mpa = new Mpa(1, "G");


        Film filmOne = Film.builder()
                .id(1)
                .name("film")
                .description("film description")
                .releaseDate(LocalDate.of(2020, 12, 12))
                .duration(123)
                .mpa(mpa)
                .build();

        Film filmTwo = Film.builder()
                .id(2)
                .name("film two")
                .description("film two description")
                .releaseDate(LocalDate.of(2020, 12, 12))
                .duration(123)
                .mpa(mpa)
                .build();

        filmDbStorage.add(filmOne);
        filmDbStorage.add(filmTwo);

        filmLikeStorage.add(filmOne.getId(), user.getId());
        filmLikeStorage.add(filmOne.getId(), anotherUser.getId());
        filmLikeStorage.add(filmTwo.getId(), anotherUser.getId());

        Collection<Film> filmRecommendations = userStorage.showRecommendations(user.getId());

        filmTwo.setLikes(1);

        assertThat(filmRecommendations)
                .isNotNull()
                .isNotEmpty()
                .containsExactlyElementsOf(List.of(filmTwo));
    }
}