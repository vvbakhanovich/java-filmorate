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
@RequestMapping("/films")
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

    @PutMapping("/{filmId}")
    public ResponseEntity<Film> updateFilm(@PathVariable long filmId, @RequestBody FilmDto updatedFilmDto) {
        Film storedFilm = films.get(filmId);
        if (storedFilm != null) {
            storedFilm.setName(updatedFilmDto.getName());
            storedFilm.setDescription(updatedFilmDto.getDescription());
            storedFilm.setReleaseDate(updatedFilmDto.getReleaseDate());
            storedFilm.setDuration(updatedFilmDto.getDuration());
            return ResponseEntity.ok(storedFilm);
        } else {
            throw new FilmNotFoundException("Фильма с id " + filmId + " не найден.");
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

