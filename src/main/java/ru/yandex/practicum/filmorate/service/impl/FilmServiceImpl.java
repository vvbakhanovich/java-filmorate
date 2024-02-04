package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.FilmLikeStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.FilmMapper.toDto;
import static ru.yandex.practicum.filmorate.mapper.FilmMapper.toModel;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {

    public static final Map<String, String> ALLOWED_SORTS = Map.of(
            "year", "f.release_date",
            "likes", "likes DESC"
    );

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final FilmLikeStorage filmLikeStorage;

    private final DirectorStorage directorStorage;

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
    @Transactional
    public FilmDto getFilmById(final long filmId) {
        filmStorage.findById(filmId);
        log.info("Фильм с id {} найден.", filmId);
        return toDto(filmStorage.findById(filmId));
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
     * @param userId идентификатор пользователя лайк которого требуется удалить.
     * @return фильм, у которого удалили лайк.
     */
    @Override
    @Transactional
    public FilmDto removeLike(final long filmId, final long userId) {
        filmStorage.findById(filmId);
        userStorage.findById(userId);
        filmLikeStorage.remove(filmId, userId);
        log.info("Пользователь с id {} удалил лайк фильма с id {}", userId, filmId);
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
     * Получение списка фильмов режиссера, отсортированных по количеству лайков или году выпуска.
     *
     * @param directorId идентификатор режиссера.
     * @param sortBy     поле сортировки.
     * @return список фильмов режиссера.
     */
    @Override
    @Transactional
    public Collection<FilmDto> getFilmsFromDirector(final long directorId, final String sortBy) {
        if (!ALLOWED_SORTS.containsKey(sortBy)) {
            throw new IllegalArgumentException("Поле сортировки '" + sortBy + "' не поддерживается.");
        }
        directorStorage.findById(directorId);
        return filmStorage.findFilmsFromDirectorOrderBy(directorId, ALLOWED_SORTS.get(sortBy)).stream()
                .map(FilmMapper::toDto)
                .collect(Collectors.toList());
    }
}