package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.GenreMapper.toDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenreServiceImpl implements GenreService {

    private final GenreStorage genreStorage;

    /**
     * Поиск жанра по идентификатору.
     *
     * @param id идентификатор жанра.
     * @return жанр.
     */
    @Override
    public GenreDto findById(int id) {
        log.info("Запрошен жанр с id '{}'.", id);
        return toDto(genreStorage.findById(id));
    }

    /**
     * Получение списка жанров.
     *
     * @return список всех жанров, хранящихся в БД.
     */
    @Override
    public Collection<GenreDto> findAll() {
        log.info("Запрошен список всех жанров.");
        return genreStorage.findAll().stream().map(GenreMapper::toDto).collect(Collectors.toList());
    }
}
