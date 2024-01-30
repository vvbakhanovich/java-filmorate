package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {
    private long reviewId;
    @NotBlank(message = "Содержание отзыва не может быть пустым.")
    private String content;
    @NotNull(message = "Не указана полезность отзыва.")
    private boolean isPositive;
    private long useful;
    @NotNull(message = "Не указан идентификатор пользователя.")
    private Long userId;
    @NotNull(message = "Не указан идентификатор фильма.")
    private Long filmId;
}
