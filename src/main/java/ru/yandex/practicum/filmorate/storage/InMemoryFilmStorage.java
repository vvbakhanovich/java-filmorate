package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public long addFilm(final Film film) {
        films.put(film.getId(), film);
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
