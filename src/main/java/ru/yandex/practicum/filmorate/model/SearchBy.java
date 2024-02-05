package ru.yandex.practicum.filmorate.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SearchBy {
    DIRECTOR,
    TITLE;

    public static List<String> getStringValues() {
        return Stream.of(SearchBy.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }


}
