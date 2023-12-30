package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {

    private final GenreDao genreDao;

    @Override
    public Genre findById(int id) {
        log.info("Запрошен жанр с id '{}'.", id);
        return genreDao.findGenreById(id);
    }

    @Override
    public Collection<Genre> findAll() {
        log.info("Запрошен список всех жанров.");
        return genreDao.findAll();
    }
}
