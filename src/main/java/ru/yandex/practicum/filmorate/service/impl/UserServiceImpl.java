package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.FriendshipStatus.ACKNOWLEDGED;
import static ru.yandex.practicum.filmorate.model.FriendshipStatus.NOT_ACKNOWLEDGED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final FriendshipStorage friendshipStorage;
    private final EventStorage eventStorage;

    /**
     * Сохранение пользователя в БД.
     *
     * @param userDto пользователь
     * @return пользователь с присвоенным идентификатором
     */
    @Override
    @Transactional
    public UserDto addUser(final UserDto userDto) {
        final User user = UserMapper.toModel(validateUserName(userDto));
        final User addedUser = userStorage.add(user);
        log.info("Добавление нового пользователя: {}", addedUser);
        return UserMapper.toDto(userStorage.findById(addedUser.getId()));
    }

    /**
     * Обновляются все поля класса за исключением списка друзей.
     *
     * @param updatedUserDto пользователь с новыми данными.
     * @return пользователь с обновленными данными.
     */
    @Override
    @Transactional
    public UserDto updateUser(final UserDto updatedUserDto) {
        final User updatedUser = UserMapper.toModel(validateUserName(updatedUserDto));
        final long userId = updatedUser.getId();
        userStorage.update(updatedUser);
        log.info("Обновление пользователя с id {} : {}", userId, updatedUser);
        return UserMapper.toDto(userStorage.findById(userId));
    }

    /**
     * Получение из БД списка всех пользователей. Данные включают список друзей в формате: id - статус дружбы.
     *
     * @return коллекция пользователей.
     */
    @Override
    public Collection<UserDto> getAllUser() {
        log.info("Получение списка всех пользователей.");
        return userStorage.findAll().stream().map(UserMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Получение пользователя по id.
     *
     * @param userId идентификатор пользователя.
     * @return пользователь, полученный из БД.
     */
    @Override
    public UserDto getUserById(final long userId) {
        final User user = userStorage.findById(userId);
        log.info("Пользователь с id {} найден.", userId);
        return UserMapper.toDto(user);
    }

    /**
     * Добавление пользователя в друзья. Если, пользователи взаимно подружились, то статус дружбы переходит в
     * "Подтверждено".
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор пользователя, которого требуется добавить в друзья.
     * @return пользователь с обновленным списком друзей.
     */
    @Override
    @Transactional
    public UserDto addFriend(final long userId, final long friendId) {
        final User user = userStorage.findById(userId);
        final User friend = userStorage.findById(friendId);
        Friendship friendship = new Friendship();
        friendship.setId(friendId);
        if (friend.getFriends().stream().map(Friendship::getId).anyMatch(id -> id == userId)) {
            friendship.setStatus(ACKNOWLEDGED.getStatus());
            friendshipStorage.add(userId, friendId, ACKNOWLEDGED.getId());
            friendshipStorage.update(friendId, userId, ACKNOWLEDGED.getId());
        } else {
            friendship.setStatus(NOT_ACKNOWLEDGED.getStatus());
            friendshipStorage.add(userId, friendId, NOT_ACKNOWLEDGED.getId());
        }
        user.getFriends().add(friendship);
        log.info("Пользователи с id {} и id {} стали друзьями.", userId, friendId);
        eventStorage.addEvent(EventType.FRIEND.name(), Operation.ADD.name(), friendId, userId);
        return UserMapper.toDto(userStorage.findById(userId));
    }

    /**
     * Получение списка друзей пользователя.
     *
     * @param userId идентификатор пользователя, список друзей которого требуется отобразить
     * @return список пользователей
     */
    @Override
    public Collection<UserDto> showFriendList(final long userId) {
        userStorage.findById(userId);
        log.info("Получение списка друзей пользователя с id {}.", userId);
        return userStorage.findFriendsByUserId(userId).stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Получение списка общих друзей между пользователями.
     *
     * @param userId      идентификатор первого пользователя.
     * @param otherUserId идентификатор второго пользователя.
     * @return список общих друзей между первым и вторым пользователем.
     */
    @Override
    public Collection<UserDto> findCommonFriends(final long userId, final long otherUserId) {
        userStorage.findById(userId);
        userStorage.findById(otherUserId);
        return userStorage.findCommonFriends(userId, otherUserId).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Удаление пользователя из списка друзей. Удаление происходит только у пользователя с userId. Если дружба была
     * обоюдной, удаление произойдет только у одного из них.
     *
     * @param userId   идентификатор пользователя.
     * @param friendId идентификатор друга, которого требуется исключить из списка друзей.
     */
    @Override
    @Transactional
    public void removeFriend(final long userId, final long friendId) {
        userStorage.findById(userId);
        userStorage.findById(friendId);
        friendshipStorage.remove(userId, friendId);
        eventStorage.addEvent(EventType.FRIEND.name(), Operation.REMOVE.name(), friendId, userId);
        log.info("Пользователи с id {} и {} перестали быть друзьями", userId, friendId);
    }

    /**
     * Удаление пользователя.
     *
     * @param userId идентификатор пользователя, который будет удален
     */
    @Override
    public void removeUser(long userId) {
        userStorage.remove(userId);
    }

    /**
     * Создание рекомендаций пользователю фильмов, которые ему могут понравиться. Для подготовки рекомендаций
     * выгружаем данные из таблицы film_like, находим пользователей с максимальным количеством одинаковых с нашим
     * пользователем лайков и выбираем у них для рекомендации, которые они тоже залайкали, но наш пользователь
     * в запросе их еще не видел
     *
     * @param id идентификатор пользователя
     * @return коллекцию FilmDto
     */

    @Override
    public Collection<FilmDto> showRecommendations(long id) {
        log.info("Получение списка рекомендаций фильмов для пользователя с id {}.", id);
        int positiveRating = 6;
        Map<Long, Map<Long, Integer>> usersLikes = filmStorage.getUsersAndFilmLikes();
        Map<Long, Set<Film>> usersLikedFilms = filmStorage.findAllFilmsLikedByUsers();
        int maxLikes = 0;
        Set<Film> recommendations = new HashSet<>();
        Set<Film> userLikedFilms = usersLikedFilms.get(id);
        Map<Long, Integer> userFilmIdRating = usersLikes.get(id);
        for (Long userId : usersLikes.keySet()) {
            if (userId != id) {
                Set<Film> sameFilms = new HashSet<>();
                Set<Film> anotherUserLikedFilms = usersLikedFilms.get(userId);
                for (Film film : anotherUserLikedFilms) {
                    Film sameFilm;
                    Optional<Film> optionalFilm = userLikedFilms.stream().filter(f -> f.getId() == film.getId()).findFirst();
                    if (optionalFilm.isEmpty()) {
                        continue;
                    } else {
                        sameFilm = optionalFilm.get();
                    }
                    long filmId = sameFilm.getId();
                    Map<Long, Integer> anotherUserFilmIdRating = usersLikes.get(userId);
                    if ((userFilmIdRating.get(filmId) >= positiveRating && anotherUserFilmIdRating.get(filmId) >= positiveRating) ||
                            (userFilmIdRating.get(filmId) < positiveRating && anotherUserFilmIdRating.get(filmId) < positiveRating))
                        sameFilms.add(film);
                }
                if (sameFilms.size() > maxLikes && sameFilms.size() < anotherUserLikedFilms.size()) {
                    recommendations.clear();
                    maxLikes = sameFilms.size();
                    anotherUserLikedFilms.removeAll(sameFilms);
                    recommendations.addAll(anotherUserLikedFilms);
                }
                if (userLikedFilms.size() == maxLikes) {
                    anotherUserLikedFilms.removeAll(sameFilms);
                    recommendations.addAll(anotherUserLikedFilms);
                }
            }
        }
        if (recommendations.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> recommendedFilmsId = recommendations.stream()
                .filter(film -> film.getRating() >= positiveRating)
                .map(Film::getId)
                .collect(Collectors.toSet());

        return filmStorage.findFilmsByIds(recommendedFilmsId).stream().map(FilmMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Выгрузка ленты пользователя. Запрос выгружает историй действий пользователя:
     * кого он добавлял в друзья и удалял из друзей
     * что лайкал
     * какие писал и удалял отзывы
     *
     * @param id идентификатор пользователя
     * @return коллекция FeedDto
     */

    @Override
    public Collection<FeedDto> getFeed(long id) {
        userStorage.findById(id);
        return eventStorage.getFeed(id).stream().map(FeedMapper::toDto).collect(Collectors.toList());
    }

    private UserDto validateUserName(final UserDto userDto) {
        final String validatedName = userDto.getName() == null || userDto.getName().isBlank() ?
                userDto.getLogin() : userDto.getName();
        userDto.setName(validatedName);
        return userDto;
    }
}