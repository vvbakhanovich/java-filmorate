package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.DirectorMapper.toDto;
import static ru.yandex.practicum.filmorate.mapper.DirectorMapper.toModel;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    /**
     * Добавление режиссера в БД.
     *
     * @param directorDto режиссер.
     * @return режиссер с присвоенным идентификатором.
     */
    @Transactional
    @Override
    public DirectorDto addDirector(final DirectorDto directorDto) {
        final Director director = toModel(directorDto);
        final Director addedDirector = directorStorage.add(director);
        log.info("Добавление нового режиссера: {}.", addedDirector);
        return toDto(directorStorage.findById(addedDirector.getId()));
    }

    /**
     * Получение списка всех режиссеров.
     *
     * @return список всех, хранящихся в БД.
     */
    @Override
    public Collection<DirectorDto> findAll() {
        log.info("Получение списка всех режиссеров.");
        return directorStorage.findAll().stream().map(DirectorMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Получение режиссера по идентификатору.
     *
     * @param id идентфикатор режиссера.
     * @return найденный режиссер.
     */
    @Override
    public DirectorDto getDirectorById(final long id) {
        final Director storedDirector = directorStorage.findById(id);
        log.info("Режиисер с id '{}' найден: {}.", id, storedDirector);
        return toDto(storedDirector);
    }

    /**
     * Обновление данных режиссера.
     *
     * @param updatedDirectorDto режиссер с новыми данными, которые необходимо обновить.
     * @return обновленный режиссер.
     */
    @Transactional
    @Override
    public DirectorDto updateDirector(final DirectorDto updatedDirectorDto) {
        final Director updatedDirector = toModel(updatedDirectorDto);
        long directorId = updatedDirector.getId();
        directorStorage.update(updatedDirector);
        log.info("Обновление режиссера с id '{}': {}.", directorId, updatedDirector);
        return toDto(directorStorage.findById(directorId));
    }

    /**
     * Удаление режиссера из БД.
     *
     * @param id идентификатор режиссера.
     */
    @Override
    public void removeDirector(final long id) {
        directorStorage.remove(id);
        log.info("Удаление режиссера с id '{}'", id);
    }
}
