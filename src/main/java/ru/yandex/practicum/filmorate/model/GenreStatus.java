package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@RequiredArgsConstructor
@Getter
public enum GenreStatus {
    COMEDY(1, "Комедия"),
    DRAMA(2, "Драма"),
    CARTOON(3, "Мультфильм"),
    THRILLER(4, "Триллер"),
    DOCUMENTARY(5, "Документальный"),
    ACTION(6, "Боевик");

    private final Integer id;
    private final String genreName;

    @Override
    public String toString() {
        return genreName;
    }

    @JsonCreator
    public static GenreStatus fromId(@JsonProperty("id") int id) {
        return Arrays.stream(GenreStatus.values()).filter(genre -> genre.id.equals(id)).findFirst().get();
    }

    public static GenreStatus fromString(String genre) {
        for (GenreStatus g : GenreStatus.values()) {
            if (g.genreName.equals(genre)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Неизвестный жанр: " + genre);
    }

}
