package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaDao mpaDao;

    @Override
    public Mpa findMpaById(int id) {
        return mpaDao.findById(id);
    }

    @Override
    public Collection<Mpa> findAll() {
        return mpaDao.findAll();
    }
}