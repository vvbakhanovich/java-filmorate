package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@RequiredArgsConstructor
public enum MpaStatus {
    G(1, "G"),
    PG(2, "PG"),
    PG13(3, "PG-13"),
    R(4, "R"),
    NC17(5, "NC-17");

    private final Integer id;
    private final String name;

    @Override
    public String toString() {
        return name;
    }

    @JsonCreator
    public static MpaStatus fromId(@JsonProperty("id") int id) {
        return Arrays.stream(MpaStatus.values()).filter(mpa -> mpa.id.equals(id)).findFirst().get();
    }

    public static MpaStatus fromString(String rating) {
        for (MpaStatus mpa : MpaStatus.values()) {
            if (mpa.name.equals(rating)) {
                return mpa;
            }
        }
        throw new IllegalArgumentException("Неизвестный рейтинг: " + rating);
    }
}
