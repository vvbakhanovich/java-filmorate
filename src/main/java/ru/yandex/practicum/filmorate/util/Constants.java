package ru.yandex.practicum.filmorate.util;

import java.time.format.DateTimeFormatter;

public final class Constants {
    private Constants() {

    }

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

}
