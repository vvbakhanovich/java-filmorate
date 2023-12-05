package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public FilmDto addFilm(FilmDto filmDto) {
        Film film = FilmMapper.toModel(filmDto);
        Film addedFilm = filmStorage.add(film);
        log.info("Добавление нового фильма: {}", addedFilm);
        return FilmMapper.toDto(addedFilm);
    }

    public FilmDto updateFilm(FilmDto updatedFilmDto) {
        Film updatedFilm = FilmMapper.toModel(updatedFilmDto);
        long filmId = updatedFilmDto.getId();
        if (filmStorage.update(updatedFilm)) {
            log.info("Обновление фильма с id {}: {}", filmId, updatedFilm);
            return FilmMapper.toDto(updatedFilm);
        } else {
            log.error("Фильм с id {} не был найден.", filmId);
            throw new NotFoundException("Фильма с id " + filmId + " не найден.");
        }
    }

    public Collection<FilmDto> getAllFilms() {
        log.info("Получение списка всех фильмов.");
        return filmStorage.findAll().stream().map(FilmMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }
}