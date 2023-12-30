package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaService {
    Mpa findMpaById(int id);

    Collection<Mpa> findAll();
}