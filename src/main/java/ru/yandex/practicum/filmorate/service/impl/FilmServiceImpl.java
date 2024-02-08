package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmSearchDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.FilmMapper.toDto;
import static ru.yandex.practicum.filmorate.mapper.FilmMapper.toModel;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final DirectorStorage directorStorage;

    private final EventStorage eventStorage;

    /**
     * Добавление фильма в БД.
     *
     * @param filmDto фильм.
     * @return фильм с присвоенным идентификатором.
     */
    @Override
    @Transactional
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
    @Transactional
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
        Film film = filmStorage.findById(filmId);
        log.info("Фильм с id {} найден.", filmId);
        return toDto(film);
    }

    /**
     * Постановка лайка фильму от пользователя.
     *
     * @param filmId идентификатор фильма, которому ставится лайк.
     * @param userId идентификатор пользователя, который ставит лайк.
     * @return фильм, которому поставили лайк.
     */
    @Override
    @Transactional
    public FilmDto likeFilm(final long filmId, final long userId, final int rating) {
        filmStorage.findById(filmId);
        userStorage.findById(userId);
        filmStorage.addLikeToFilm(filmId, userId, rating);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
        eventStorage.addEvent(EventType.LIKE.name(), Operation.ADD.name(), filmId, userId);
        return toDto(filmStorage.findById(filmId));
    }

    /**
     * Удаление лайка у фильма.
     *
     * @param filmId идентификатор фильма, у которого требуется удалить лайк.
     * @param userId идентификатор пользователя лайк которого требуется удалить.
     * @return фильм, у которого удалили лайк.
     */
    @Override
    @Transactional
    public FilmDto removeLike(final long filmId, final long userId) {
        filmStorage.findById(filmId);
        userStorage.findById(userId);
        filmStorage.removeLikeFromFilm(filmId, userId);
        log.info("Пользователь с id {} удалил лайк фильма с id {}", userId, filmId);
        eventStorage.addEvent(EventType.LIKE.name(), Operation.REMOVE.name(), filmId, userId);
        return toDto(filmStorage.findById(filmId));
    }

    /**
     * Удаление фильма.
     *
     * @param filmId идентификатор фильма, который будет удален
     */
    @Override
    public void removeFilm(long filmId) {
        filmStorage.remove(filmId);
    }

    /**
     * Получение списка самых популярных фильмов. Под популярностью понимается количество лайков у фильма. Чем больше
     * лайков, тем популярнее фильм.
     *
     * @param count   ограничение количества выводимых фильмов
     * @param genreId ограничение выводимых фильмов по жанру
     * @param year    ограничение выводимых фильмов по году
     * @return список фильмов.
     */

    @Override
    public Collection<FilmDto> getMostPopularFilms(final int count, final Integer genreId, final Integer year) {
        return filmStorage.findMostLikedFilms(count, genreId, year)
                .stream()
                .map(FilmMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Поиск фильмов по названию и по режиссеру.
     *
     * @param search query - текст для поиска
     * @param search by - может принимать значения director (поиск по режиссёру), title (поиск по названию),
     *               либо оба значения через запятую при поиске одновременно и по режиссеру и по названию.
     * @return список фильмов.
     */
    @Override
    public Collection<FilmDto> searchFilms(FilmSearchDto search) {
        if (!SearchBy.getStringValues().containsAll(search.getBy())) {
            throw new IllegalArgumentException("Поле сортировки '" + search.getBy() + "' не поддерживается.");
        }
        return filmStorage.searchFilms(search)
                .stream()
                .map(FilmMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение списка фильмов режиссера, отсортированных по количеству лайков или году выпуска.
     *
     * @param directorId идентификатор режиссера.
     * @param sortBy     поле сортировки.
     * @return список фильмов режиссера.
     */
    @Override
    @Transactional
    public Collection<FilmDto> getFilmsFromDirector(final long directorId, final String sortBy) {
        if (!SortBy.getStringValues().contains(sortBy)) {
            throw new IllegalArgumentException("Поле сортировки '" + sortBy + "' не поддерживается.");
        }
        String sort = SortBy.fromString(sortBy).getSql();
        directorStorage.findById(directorId);
        return filmStorage.findFilmsFromDirectorOrderBy(directorId, sort).stream()
                .map(FilmMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение списка общих фильмов с сортировкой по их популярности.
     *
     * @param userId   идентификатор первого пользователя.
     * @param friendId идентификатор второго пользователя.
     * @return список общих фильмов между пользователями.
     */
    @Override
    public Collection<FilmDto> getCommonFilms(long userId, long friendId) {
        userStorage.findById(userId);
        userStorage.findById(friendId);
        return filmStorage.findCommonFilms(userId, friendId).stream()
                .map(FilmMapper::toDto)
                .collect(Collectors.toList());
    }
}
