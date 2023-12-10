package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmStorage implements Storage<Film> {

    private final IdGenerator<Long> idGenerator;

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film add(final Film film) {
        film.setId(idGenerator.generateId());
        films.put(film.getId(), film);
        log.info("Сохранен фильм: {}", film);
        return film;
    }

    @Override
    public void remove(final long filmId) {
        if (films.containsKey(filmId)) {
            films.remove(filmId);
            log.info("Удален фильм с id {}", filmId);
        } else {
            log.error("Фильм с id {} не был найден.", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    @Override
    public void update(final Film updatedFilm) {
        long filmId = updatedFilm.getId();
        if (films.containsKey(filmId)) {
            films.put(filmId, updatedFilm);
            log.info("Обновлен фильм {}", updatedFilm);
        } else {
            log.error("Фильм с id {} не был найден.", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(long filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else {
            log.error("Фильм с id {} не был найден.", filmId);
            throw new NotFoundException("Фильм с id " + filmId + " не найден.");
        }
    }
}
