package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmSearchDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Validated
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
    public FilmDto addMarkToFilm(@PathVariable long id, @PathVariable long userId,
                                 @Min(value = 1, message = "Рейтинг не может быть ниже 1.")
                                 @Max(value = 10, message = "Рейтинг не должен превышать 10.") Integer mark) {
        return filmService.addMarkToFilm(id, userId, mark);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public FilmDto removeMarkFromFilm(@PathVariable long id, @PathVariable long userId) {
        return filmService.removeMarkFromFilm(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> getMostPopularFilms(@RequestParam(defaultValue = "10") int count,
                                                   @RequestParam(required = false) Integer genreId,
                                                   @RequestParam(required = false) @Min(1895) Integer year) {
        return filmService.getMostPopularFilms(count, genreId, year);
    }

    @DeleteMapping("/{id}")
    public void removeFilm(@PathVariable long id) {
        filmService.removeFilm(id);
    }

    @GetMapping("/search")
    public Collection<FilmDto> searchFilms(@Valid FilmSearchDto search) {
        return filmService.searchFilms(search);
    }

    @GetMapping("/director/{directorId}")
    public Collection<FilmDto> getFilmsFromDirector(@PathVariable long directorId, @RequestParam String sortBy) {
        return filmService.getFilmsFromDirector(directorId, sortBy);
    }

    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(@RequestParam long userId, @RequestParam long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

}

