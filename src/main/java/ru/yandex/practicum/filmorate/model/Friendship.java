package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Friendship {
    ACK(1, "Acknowledged"),
    NOT_ACK(2, "Not acknowledged");

    private final Integer statusId;
    private final String status;

    @Override
    @JsonValue
    public String toString() {
        return status;
    }

    public static Friendship fromString(String s) {
        for (Friendship fs : Friendship.values()) {
            if (fs.status.contains(s)) {
                return fs;
            }
        }
        throw new IllegalArgumentException("Статуса дружбы '" + s + "' не существует.");
    }
}
