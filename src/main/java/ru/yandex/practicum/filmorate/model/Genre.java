package ru.yandex.practicum.filmorate.model;

public enum Genre {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private final String genreName;

    Genre(String genreName) {
        this.genreName = genreName;
    }

    public static Genre fromString(String genre) {
        for (Genre g : Genre.values()) {
            if (g.genreName.equals(genre)) {
                return g;
            }
        }
        throw new IllegalArgumentException("Неизвестный жанр: " + genre);
    }

}
