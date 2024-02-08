package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.yandex.practicum.filmorate.dto.ReviewDto;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.validation.ValidationTestUtils.VALIDATOR;
import static ru.yandex.practicum.filmorate.validation.ValidationTestUtils.dtoHasErrorMessage;

public class ReviewValidationTest {

    @Test
    @DisplayName("Проверка возможности добавить отзыв с корректными полями")
    public void createReview() {
        ReviewDto reviewDto = ReviewDto.builder()
                .reviewId(1)
                .content("content")
                .isPositive(true)
                .useful(1)
                .filmId(1L)
                .userId(1L)
                .build();

        assertTrue(VALIDATOR.validate(reviewDto).isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   ", "     "})
    @DisplayName("Проверка невозможности добавить отзыв с пустым полем content")
    public void createReviewWithoutContent(String content) {
        ReviewDto reviewDto = ReviewDto.builder()
                .reviewId(1)
                .content(content)
                .isPositive(true)
                .useful(1)
                .filmId(1L)
                .userId(1L)
                .build();

        assertTrue(dtoHasErrorMessage(reviewDto, "Содержание отзыва не может быть пустым."));

    }

    @Test
    @DisplayName("Проверка невозможности добавить отзыв, если content == null")
    public void createReviewWithNullContent() {
        ReviewDto reviewDto = ReviewDto.builder()
                .reviewId(1)
                .content(null)
                .isPositive(true)
                .useful(1)
                .filmId(1L)
                .userId(1L)
                .build();

        assertTrue(dtoHasErrorMessage(reviewDto, "Содержание отзыва не может быть пустым."));
    }

    @Test
    @DisplayName("Проверка невозможности добавить отзыв, если не указан filmId")
    public void createReviewWithNullFilmId() {
        ReviewDto reviewDto = ReviewDto.builder()
                .reviewId(1)
                .content("content")
                .isPositive(true)
                .useful(1)
                .filmId(null)
                .userId(1L)
                .build();

        assertTrue(dtoHasErrorMessage(reviewDto, "Не указан идентификатор фильма."));
    }

    @Test
    @DisplayName("Проверка невозможности добавить отзыв, если не указан userId")
    public void createReviewWithNullUserId() {
        ReviewDto reviewDto = ReviewDto.builder()
                .reviewId(1)
                .content("content")
                .isPositive(true)
                .useful(1)
                .filmId(1L)
                .userId(null)
                .build();

        assertTrue(dtoHasErrorMessage(reviewDto, "Не указан идентификатор пользователя."));
    }

    @Test
    @DisplayName("Проверка невозможности добавить отзыв, если не указана полезность")
    public void createReviewWithNullIsPositive() {
        ReviewDto reviewDto = ReviewDto.builder()
                .reviewId(1)
                .content("content")
                .isPositive(null)
                .useful(1)
                .filmId(1L)
                .userId(1L)
                .build();

        assertTrue(dtoHasErrorMessage(reviewDto, "Не указана полезность отзыва."));
    }
}
