package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto addFilm(@Valid @RequestBody FilmDto filmDto) {
        return filmService.addFilm(filmDto);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody FilmDto updatedFilmDto) {
        return filmService.updateFilm(updatedFilmDto);
    }

    @GetMapping
    public Collection<FilmDto> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable long id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmDto likeFilm(@PathVariable long id, @PathVariable long userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public FilmDto removeLike(@PathVariable long id, @PathVariable long userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getMostPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        return filmService.getMostPopularFilms(count);
    }
}

