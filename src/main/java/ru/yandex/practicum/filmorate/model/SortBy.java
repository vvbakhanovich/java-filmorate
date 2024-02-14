package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum SortBy {
    YEAR("f.release_date"),
    LIKES("COUNT(fm.USER_ID) DESC"),
    RATING("rating DESC");

    private final String sql;

    public static List<String> getStringValues() {
        return Stream.of(SortBy.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public static SortBy fromString(String text) {
        return Arrays.stream(SortBy.values())
                .filter(sortBy -> sortBy.name().equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Данная сортировка не поддерживатется"));
    }
}



