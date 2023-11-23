package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private long userId = 1;
    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody FilmDto filmDto) {
        Film film = Film.build(generateId(),filmDto.getName(), filmDto.getDescription(), filmDto.getReleaseDate(),
                filmDto.getDuration());
        films.put(film.getId(), film);
        log.info("Добавление нового фильма: " + film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    // Решил задавать id обновляемого фильма в uri, поэтому некоторые тесты для постман, которые были прикреплены к тз
    // не проходят.
    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody FilmDto updatedFilmDto) {
        long filmId = updatedFilmDto.getId();
        Film storedFilm = films.get(filmId);
        if (storedFilm != null) {
            storedFilm.setName(updatedFilmDto.getName());
            storedFilm.setDescription(updatedFilmDto.getDescription());
            storedFilm.setReleaseDate(updatedFilmDto.getReleaseDate());
            storedFilm.setDuration(updatedFilmDto.getDuration());
            log.info("Обновление фильма с id " + filmId + ": " + storedFilm);
            return ResponseEntity.ok(storedFilm);
        } else {
            log.warn("Фильм с id " + filmId + " не был найден.");
            throw new FilmNotFoundException("Фильма с id " + filmId + " не найден.");
        }
    }

    @GetMapping
    public ResponseEntity<ArrayList<Film>> getAllFilms() {
        log.info("Получение списка всех фильмов.");
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }

    private long generateId() {
        return userId++;
    }
}

