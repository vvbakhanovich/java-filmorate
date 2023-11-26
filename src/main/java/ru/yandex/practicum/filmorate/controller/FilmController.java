package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private long filmId = 1;
    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public ResponseEntity<FilmDto> addFilm(@Valid @RequestBody FilmDto filmDto) {
        Film film = FilmMapper.toModel(filmDto);
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Добавление нового фильма: " + film);
        return new ResponseEntity<>(FilmMapper.toDto(film), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<FilmDto> updateFilm(@Valid @RequestBody FilmDto updatedFilmDto) {
        long filmId = updatedFilmDto.getId();
        Film storedFilm = films.get(filmId);
        if (storedFilm != null) {
            storedFilm.setName(updatedFilmDto.getName());
            storedFilm.setDescription(updatedFilmDto.getDescription());
            storedFilm.setReleaseDate(updatedFilmDto.getReleaseDate());
            storedFilm.setDuration(updatedFilmDto.getDuration());
            log.info("Обновление фильма с id " + filmId + ": " + storedFilm);
            return ResponseEntity.ok(FilmMapper.toDto(storedFilm));
        } else {
            log.warn("Фильм с id " + filmId + " не был найден.");
            throw new NotFoundException("Фильма с id " + filmId + " не найден.");
        }
    }

    @GetMapping
    public ResponseEntity<ArrayList<FilmDto>> getAllFilms() {
        log.info("Получение списка всех фильмов.");
        return ResponseEntity.ok(new ArrayList<>(films.values().stream().map(FilmMapper::toDto)
                .collect(Collectors.toList())));
    }

    private long generateId() {
        return filmId++;
    }
}

