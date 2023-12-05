package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryFilmStorage implements Storage<Film> {

    IdGenerator<Long> idGenerator;

    @Autowired
    public InMemoryFilmStorage(IdGenerator<Long> idGenerator) {
        this.idGenerator = idGenerator;
    }

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public long add(final Film film) {
        films.put(idGenerator.generateId(), film);
        return film.getId();
    }

    @Override
    public boolean remove(final long filmId) {
        return films.remove(filmId) != null;
    }

    @Override
    public boolean update(final Film updatedFilm) {
        if (films.containsKey(updatedFilm.getId())) {
            films.put(updatedFilm.getId(), updatedFilm);
            return true;
        }
        return false;
    }
}
