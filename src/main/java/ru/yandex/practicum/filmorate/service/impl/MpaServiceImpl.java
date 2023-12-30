package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class MpaServiceImpl implements MpaService {

    private final MpaDao mpaDao;

    @Override
    public Mpa findMpaById(int id) {
        log.info("Запрошен Mpa с id '{}'.", id);
        return mpaDao.findById(id);
    }

    @Override
    public Collection<Mpa> findAll() {
        log.info("Запрошен список всех Mpa.");
        return mpaDao.findAll();
    }
}