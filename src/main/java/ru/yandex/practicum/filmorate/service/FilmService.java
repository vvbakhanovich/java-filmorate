package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.FilmMapper.toDto;
import static ru.yandex.practicum.filmorate.mapper.FilmMapper.toModel;

@Service
@Slf4j
public class FilmService {

    FilmStorage filmStorage;

    UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public FilmDto addFilm(FilmDto filmDto) {
        Film film = toModel(filmDto);
        Film addedFilm = filmStorage.add(film);
        log.info("Добавление нового фильма: {}", addedFilm);
        return toDto(addedFilm);
    }

    public FilmDto updateFilm(FilmDto updatedFilmDto) {
        Film updatedFilm = toModel(updatedFilmDto);
        long filmId = updatedFilmDto.getId();
        if (filmStorage.update(updatedFilm)) {
            log.info("Обновление фильма с id {}: {}", filmId, updatedFilm);
            return toDto(updatedFilm);
        } else {
            log.error("Фильм с id {} не был найден.", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    public Collection<FilmDto> getAllFilms() {
        log.info("Получение списка всех фильмов.");
        return filmStorage.findAll().stream().map(FilmMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }

    public FilmDto getFilmById(long filmId) {
        Film film = filmStorage.findById(filmId);
        if (film != null) {
            log.info("Фильм с id {} найден.", filmId);
            return toDto(film);
        } else {
            log.error("Фильм с id {} не был найден.", filmId);
            throw new NotFoundException("Фильм id " + filmId + " не найден.");
        }
    }

    public FilmDto likeFilm(long filmId, long userId) {
        Film film = filmStorage.findById(filmId);
        User user = userStorage.findById(userId);
        if (film != null) {
            if (user != null) {
                film.getLikes().add(userId);
                log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
                return toDto(film);
            } else {
                log.error("Пользователь с id {} не был найден.", userId);
                throw new NotFoundException("Пользователь id " + userId + " не найден");
            }
        } else {
            log.error("Фильм с id {} не был найден.", filmId);
            throw new NotFoundException("Пользователь id " + userId + " не найден.");
        }
    }
}