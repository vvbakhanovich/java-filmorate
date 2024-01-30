package ru.yandex.practicum.filmorate.service;


import ru.yandex.practicum.filmorate.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto addReview(ReviewDto reviewDto);

    ReviewDto getReviewById(long id);

    ReviewDto updateReview(ReviewDto updatedReviewDto);

    void deleteReview(long id);

    List<ReviewDto> getReviewsByFilmId(Long filmId, int count);

    ReviewDto addLikeToReview(long id, long userId);

    ReviewDto addDislikeToReview(long id, long userId);

    ReviewDto deleteLikeFromReview(long id, long userId);

    ReviewDto deleteDislikeFromReview(long id, long userId);
}
