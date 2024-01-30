package ru.yandex.practicum.filmorate.model;

public enum ReviewLike {
    LIKE("like"),
    DISLIKE("dislike");

    private final String type;

    ReviewLike(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
