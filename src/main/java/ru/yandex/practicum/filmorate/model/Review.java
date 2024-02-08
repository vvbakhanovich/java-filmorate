package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {
    private long reviewId;
    private String content;
    private boolean isPositive;
    private long useful;
    private long userId;
    private long filmId;
}
