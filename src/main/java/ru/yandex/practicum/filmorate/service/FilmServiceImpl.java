package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.FilmMapper.toDto;
import static ru.yandex.practicum.filmorate.mapper.FilmMapper.toModel;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final Storage<Film> filmStorage;

    private final Storage<User> userStorage;

    @Override
    public FilmDto addFilm(final FilmDto filmDto) {
        Film film = toModel(filmDto);
        Film addedFilm = filmStorage.add(film);
        log.info("Добавление нового фильма: {}", addedFilm);
        return toDto(addedFilm);
    }

    @Override
    public FilmDto updateFilm(final FilmDto updatedFilmDto) {
        Film updatedFilm = toModel(updatedFilmDto);
        long filmId = updatedFilmDto.getId();
        filmStorage.update(updatedFilm);
        log.info("Обновление фильма с id {}: {}", filmId, updatedFilm);
        return toDto(updatedFilm);
    }

    @Override
    public Collection<FilmDto> getAllFilms() {
        log.info("Получение списка всех фильмов.");
        return filmStorage.findAll().stream().map(FilmMapper::toDto).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public FilmDto getFilmById(final long filmId) {
        Film film = filmStorage.findById(filmId);
        log.info("Фильм с id {} найден.", filmId);
        return toDto(film);
    }

    @Override
    public FilmDto likeFilm(final long filmId, final long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        film.getLikes().add(userId);
        log.info("Пользователь с id {} поставил лайк фильму с id {}", userId, filmId);
        return toDto(film);
    }

    @Override
    public FilmDto removeLike(final long filmId, final long userId) {
        Film film = filmStorage.findById(filmId);
        userStorage.findById(userId);
        film.getLikes().remove(userId);
        log.info("Пользователь с id {} удалил лайк фильма с id {}", userId, filmId);
        return toDto(film);
    }

    @Override
    public Collection<FilmDto> getMostPopularFilms(final int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film o) -> o.getLikes().size()).reversed())
                .limit(count)
                .map(FilmMapper::toDto)
                .collect(Collectors.toList());
    }
}