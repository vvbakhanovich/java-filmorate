package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {

    private final GenreStorage genreStorage;

    @Override
    public Genre findById(int id) {
        log.info("Запрошен жанр с id '{}'.", id);
        return genreStorage.findById(id);
    }

    @Override
    public Collection<Genre> findAll() {
        log.info("Запрошен список всех жанров.");
        return genreStorage.findAll();
    }
}
