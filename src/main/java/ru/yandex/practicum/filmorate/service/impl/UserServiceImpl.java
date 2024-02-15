package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.FriendshipStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.RecommendationsCurrentParams;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.FeedMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.FilmMark;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.FriendshipStatus.ACKNOWLEDGED;
import static ru.yandex.practicum.filmorate.model.FriendshipStatus.NOT_ACKNOWLEDGED;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private static final int MIN_POSITIVE_RATING_VALUE = 6;

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
     * Получение списка рекомендованных к просмотру фильмов, которые могут понравиться пользователю. Алгоритм
     * выбора рекомендаций определяет пользователя с наиболее схожими оценками с пользователем, который хочет получить
     * рекомендации, и возвращает те фильмы, которые не были оценены искомым пользователем и у которых положительный
     * рейтинг.
     *
     * @param requesterId идентификатор пользователя, который хочет получить рекомендации.
     * @return список рекомендованных фильмов.
     */

    @Override
    public Collection<FilmDto> showRecommendations(long requesterId) {
        log.info("Получение списка рекомендаций фильмов для пользователя с id {}.", requesterId);
        Map<Long, Set<FilmMark>> usersFilmMarks = filmStorage.findUserIdFilmMarks();
        Set<FilmMark> requesterFilmMarks = usersFilmMarks.get(requesterId);
        Long userIdWithClosestMarks = findUserIdWithClosestMarks(usersFilmMarks, requesterId);
        if (userIdWithClosestMarks == null) {
            return Collections.emptyList();
        }
        Set<Long> matchedUserMarkedFilmIds = usersFilmMarks.get(userIdWithClosestMarks).stream()
                .map(FilmMark::getFilmId)
                .collect(Collectors.toSet());
        matchedUserMarkedFilmIds.removeAll(searchedUserFilmMarks.stream()
                .map(FilmMark::getFilmId)
                .collect(Collectors.toSet()));
        return filmStorage.findFilmsByIds(matchedUserMarkedFilmIds).stream()
                .filter(film -> film.getRating() >= MIN_POSITIVE_RATING_VALUE)
                .map(FilmMapper::toDto)
                .collect(Collectors.toList());
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

    private RecommendationsCurrentParams compareToCurrentUserMarks(Set<FilmMark> searchedUserFilmMarks,
                                                                   Set<FilmMark> currentUserFilmMarks) {
        RecommendationsCurrentParams currentParams = new RecommendationsCurrentParams();
        for (FilmMark filmMark : searchedUserFilmMarks) {
            long filmId = filmMark.getFilmId();
            currentParams.setNumberOfLikedFilms(currentUserFilmMarks.size());
            Optional<FilmMark> currentUserFilmMark = currentUserFilmMarks.stream()
                    .filter(currentFilmMark -> currentFilmMark.getFilmId() == filmId)
                    .findAny();
            int rateDiff = currentUserFilmMark
                    .map(mark -> filmMark.getMark() - mark.getMark())
                    .orElseGet(filmMark::getMark);
            currentParams.setDiff(currentParams.getDiff() + rateDiff);
            if (currentUserFilmMark.isPresent()) {
                currentParams.setNumberOfMatches(currentParams.getNumberOfMatches() + 1);
            }
        }
        return currentParams;
    }

    private Long findUserIdWithClosestMarks(Map<Long, Set<FilmMark>> usersFilmMarks, long searchedUserId) {
        double closestMarksDiff = Double.MAX_VALUE;
        Long userIdWithClosestMarks = null;
        int numberOfLikedFilms = 0;
        for (Long userId : usersFilmMarks.keySet()) {
            if (userId == searchedUserId) {
                continue;
            }
            Set<FilmMark> currentUserFilmMarks = usersFilmMarks.get(userId);
            RecommendationsCurrentParams currentParams =
                    compareToCurrentUserMarks(usersFilmMarks.get(searchedUserId), currentUserFilmMarks);
            if (currentParams.getNumberOfMatches() == 0) {
                continue;
            }
            double marksDiff =
                    Math.abs((double) currentParams.getDiff() / currentParams.getNumberOfMatches());
            if (marksDiff < closestMarksDiff) {
                closestMarksDiff = marksDiff;
                userIdWithClosestMarks = userId;
                numberOfLikedFilms = currentParams.getNumberOfLikedFilms();
            }
            if (marksDiff == closestMarksDiff && currentParams.getNumberOfLikedFilms() > numberOfLikedFilms) {
                userIdWithClosestMarks = userId;
            }
        }
        return userIdWithClosestMarks;
    }
}