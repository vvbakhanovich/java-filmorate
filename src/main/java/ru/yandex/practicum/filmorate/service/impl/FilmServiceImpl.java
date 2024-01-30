package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmLikeStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Comparator;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.FilmMapper.toDto;
import static ru.yandex.practicum.filmorate.mapper.FilmMapper.toModel;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final FilmLikeStorage filmLikeStorage;

    /**
     * Добавление фильма в БД.
     *
     * @param filmDto фильм.
     * @return фильм с присвоенным идентификатором.
     */
    @Override
    public FilmDto addFilm(final FilmDto filmDto) {
        final Film film = toModel(filmDto);
        final Film addedFilm = filmStorage.add(film);
        log.info("Добавление нового фильма: {}", addedFilm);
        return toDto(filmStorage.findById(addedFilm.getId()));
    }

    /**
     * Обновление данных фильма.
     *
     * @param updatedFilmDto фильм с новыми данными и идентификатором фильма, данные которого требуется обновить.
     * @return обновленный фильм.
     */
    @Override
    public FilmDto updateFilm(final FilmDto updatedFilmDto) {
        final Film updatedFilm = toModel(updatedFilmDto);
        final long filmId = updatedFilmDto.getId();
        filmStorage.update(updatedFilm);
        log.info("Обновление фильма с id {}: {}", filmId, updatedFilm);
        return toDto(filmStorage.findById(filmId));
    }

    /**
     * Получение списка всех фильмов.
     *
     * @return список всех фильмов, хранящихся в БД.
     */
    @Override
    public Collection<FilmDto> getAllFilms() {
        log.info("Получение списка всех фильмов.");
        return filmStorage.findAll().stream().map(FilmMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Поиск фильма по идентификатору.
     *
     * @param filmId идентификатор фильма.
     * @return найденный фильм.
     */
    @Override
    public FilmDto getFilmById(final long filmId) {
        filmStorage.findById(filmId);
        log.info("Фильм с id {} найден.", filmId);
        return toDto(filmStorage.findById(filmId));
    }

    /**
     * Постановка лайка фильму от пользователя.
     *
     * @param filmId идентификатор фильма, которму ставится лайк.
     * @param userId идентификатор пользователя, который ставит лайк.
     * @return фильм, которому поставили лайк.
     */
    @Override
    public FilmDto likeFilm(final long filmId, final long userId) {
        filmStorage.findById(filmId);
        userStorage.findById(userId);
        filmLikeStorage.add(filmId, userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
        return toDto(filmStorage.findById(filmId));
    }

    /**
     * Удаление лайка у фильма.
     *
     * @param filmId идентификатор фильма, у которого требуется удалить лайк.
     * @param userId идентфикатор пользователя лайк которого требуется удалить.
     * @return фильм, у которого удалили лайк.
     */
    @Override
    public FilmDto removeLike(final long filmId, final long userId) {
        filmStorage.findById(filmId);
        userStorage.findById(userId);
        filmLikeStorage.remove(filmId, userId);
        log.info("Пользователь с id {} удалил лайк фильма с id {}", userId, filmId);
        return toDto(filmStorage.findById(filmId));
    }

    /**
     * Получение списка самых популярных фильмов. Под популярностью понимается количество лайков у фильма. Чем больше
     * лайков, тем популярнее фильм.
     *
     * @param count ограничение количества выводимых фильмов
     * @return список фильмов.
     */
    @Override
    public Collection<FilmDto> getMostPopularFilms(final int count) {
        return filmStorage.findMostLikedFilmsLimitBy(count).stream().map(FilmMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Collection<FilmDto> getCommonFilms(long userId, long friendId) {

        Set<Long> userFilms = filmLikeStorage.findLikedFilmsByUser(userId);
        Set<Long> friendFilms = filmLikeStorage.findLikedFilmsByUser(friendId);

        userFilms.retainAll(friendFilms);

        return userFilms.stream()
                .map(filmId -> filmStorage.findById(filmId))
                .filter(Objects::nonNull)
                .map(FilmMapper::toDto)
                .sorted(Comparator.comparingLong(FilmDto::getLikes).reversed())
                .collect(Collectors.toList());
    }
}