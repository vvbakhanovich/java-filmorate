package ru.yandex.practicum.filmorate.service;


import ru.yandex.practicum.filmorate.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto addReview(ReviewDto reviewDto);

    ReviewDto getReviewById(long id);

    ReviewDto updateReview(ReviewDto updatedReviewDto);

    void deleteReview(long id);

    List<ReviewDto> getReviewsByFilmId(Long filmId, int count);

    ReviewDto addLikeOrDislikeToReview(long id, long userId, String type);

    ReviewDto deleteLikeOrDislikeFromReview(long id, long userId, String type);
}
