package ru.yandex.practicum.filmorate.controller;

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
public class FilmController {
    private long userId = 1;
    Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public ResponseEntity<Film> addFilm(@RequestBody FilmDto filmDto) {
        Film film = Film.build(generateId(),filmDto.getName(), filmDto.getDescription(), filmDto.getReleaseDate(),
                filmDto.getDuration());
        films.put(film.getFilmId(), film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Film> updateFilm(@RequestBody FilmDto updatedfilmDto, @RequestParam long filmId) {
        Film storedFilm = films.get(filmId);
        if (storedFilm != null) {
            storedFilm.setName(updatedfilmDto.getName());
            storedFilm.setDescription(updatedfilmDto.getDescription());
            storedFilm.setReleaseDate(updatedfilmDto.getReleaseDate());
            storedFilm.setDuration(updatedfilmDto.getDuration());
            return ResponseEntity.ok(storedFilm);
        } else {
            throw new FilmNotFoundException("Фильма с id " + filmId + " не существует.");
        }
    }

    @GetMapping
    public ResponseEntity<ArrayList<Film>> getAllFilms() {
        return ResponseEntity.ok(new ArrayList<>(films.values()));
    }

    private long generateId() {
        return userId++;
    }
}

