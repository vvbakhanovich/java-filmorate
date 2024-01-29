package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

@UtilityClass
public class MpaMapper {

    public static MpaDto toDto(Mpa mpa) {
        return new MpaDto(mpa.getId(), mpa.getName());
    }

    public static Mpa toModel(MpaDto mpaDto) {
        return new Mpa(mpaDto.getId(), mpaDto.getName());
    }
}
