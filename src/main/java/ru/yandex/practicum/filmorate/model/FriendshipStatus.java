package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FriendshipStatus {
    ACK("Acknowledged", 1),
    NOT_ACK("Not acknowledged", 2);

    private final String status;
    @Getter
    private final Integer statusId;

    @Override
    public String toString() {
        return status;
    }

    public static FriendshipStatus fromString(String s) {
        for (FriendshipStatus fs : FriendshipStatus.values()) {
            if (fs.status.contains(s)) {
                return fs;
            }
        }
        throw new IllegalArgumentException("Статуса дружбы '" + s + "' не существует.");
    }
}
