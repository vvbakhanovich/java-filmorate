package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

@UtilityClass
public class ReviewMapper {

    public static ReviewDto toDto(Review review) {
        return ReviewDto.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .isPositive(review.isPositive())
                .useful(review.getUseful())
                .filmId(review.getFilmId())
                .userId(review.getUserId())
                .build();
    }

    public static Review toModel(ReviewDto reviewDto) {
        return Review.builder()
                .reviewId(reviewDto.getReviewId())
                .content(reviewDto.getContent())
                .isPositive(reviewDto.getIsPositive())
                .useful(reviewDto.getUseful())
                .filmId(reviewDto.getFilmId())
                .userId(reviewDto.getUserId())
                .build();
    }
}
