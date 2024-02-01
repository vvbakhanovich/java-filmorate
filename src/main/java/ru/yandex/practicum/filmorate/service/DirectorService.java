package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.DirectorDto;

import java.util.Collection;

public interface DirectorService {
    DirectorDto addDirector(DirectorDto directorDto);

    Collection<DirectorDto> findAll();

    DirectorDto getDirectorById(long id);

    DirectorDto updateDirector(DirectorDto updatedDirectorDto);

    void removeDirector(long id);
}
