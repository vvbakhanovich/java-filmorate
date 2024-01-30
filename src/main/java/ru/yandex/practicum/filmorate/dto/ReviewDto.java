package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {
    private long reviewId;
    @NotBlank(message = "Отзыв не может быть пустой.")
    private String content;
    @NotBlank(message = "Не указана полезность отзыва.")
    private boolean isPositive;
    private long useful;
    private long userId;
    private long filmId;
}
