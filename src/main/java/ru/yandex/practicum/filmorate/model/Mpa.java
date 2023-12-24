package ru.yandex.practicum.filmorate.model;

public enum Mpa {
    G ("G"),
    PG ("PG"),
    PG13 ("PG-13"),
    R ("R"),
    NC17 ("NC-17");

    private final String ratingName;

    Mpa (String ratingName) {
        this.ratingName = ratingName;
    }

    public static Mpa fromString(String rating) {
        for (Mpa mpa : Mpa.values()) {
            if (mpa.ratingName.equals(rating)) {
                return mpa;
            }
        }
        throw new IllegalArgumentException("Неизвестный рейтинг: " + rating);
    }
}
