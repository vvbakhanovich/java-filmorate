package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    IdGenerator<Long> idGenerator;

    @Autowired
    public InMemoryFilmStorage(IdGenerator<Long> idGenerator) {
        this.idGenerator = idGenerator;
    }

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film add(final Film film) {
        film.setId(idGenerator.generateId());
        films.put(film.getId(), film);
        log.info("Сохранен фильм: {}", film);
        return film;
    }

    @Override
    public boolean remove(final long filmId) {
        log.info("Удален фильм с id {}", filmId);
        return films.remove(filmId) != null;
    }

    @Override
    public boolean update(final Film updatedFilm) {
        if (films.containsKey(updatedFilm.getId())) {
            films.put(updatedFilm.getId(), updatedFilm);
            log.info("Обновлен фильм {}", updatedFilm);
            return true;
        }
        return false;
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findById(long id) {
        return films.get(id);
    }
}
