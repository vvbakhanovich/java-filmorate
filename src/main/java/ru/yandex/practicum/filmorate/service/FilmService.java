package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.FilmMapper.*;

@Service
@Slf4j
public class FilmService {

    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
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
}