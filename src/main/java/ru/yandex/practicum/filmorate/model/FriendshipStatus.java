package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FriendshipStatus {
    ACKNOWLEDGED(1, "Acknowledged"),
    NOT_ACKNOWLEDGED(2, "Not acknowledged");

    private final int id;
    private final String status;

    @Override
    public String toString() {
        return status;
    }
}
