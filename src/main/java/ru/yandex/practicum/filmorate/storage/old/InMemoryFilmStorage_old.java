package ru.yandex.practicum.filmorate.storage.old;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.IdGenerator;

import java.util.HashMap;
import java.util.Map;

public class InMemoryFilmStorage_old implements FilmStorage {

    IdGenerator<Long> idGenerator;

    @Autowired
    public InMemoryFilmStorage_old(IdGenerator<Long> idGenerator) {
        this.idGenerator = idGenerator;
    }

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public long addFilm(final Film film) {
        films.put(idGenerator.generateId(), film);
        return film.getId();
    }

    @Override
    public boolean removeFilm(final long filmId) {
        return films.remove(filmId) != null;
    }

    @Override
    public boolean updateFilm(final Film updatedFilm) {
        if (films.containsKey(updatedFilm.getId())) {
            films.put(updatedFilm.getId(), updatedFilm);
            return true;
        }
        return false;
    }
}
