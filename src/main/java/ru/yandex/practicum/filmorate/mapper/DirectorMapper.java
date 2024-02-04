package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@UtilityClass
public class DirectorMapper {

    public static DirectorDto toDto(Director director) {
        return DirectorDto.builder()
                .id(director.getId())
                .name(director.getName())
                .build();
    }

    public static Director toModel(DirectorDto directorDto) {
        return Director.builder()
                .id(directorDto.getId())
                .name(directorDto.getName())
                .build();
    }
}
