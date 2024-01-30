package ru.yandex.practicum.filmorate.service;


import ru.yandex.practicum.filmorate.dto.ReviewDto;

public interface ReviewService {
    ReviewDto addReview(ReviewDto reviewDto);

    ReviewDto getReviewById(long id);

    ReviewDto updateReview(ReviewDto updatedReviewDto);

    void deleteReview(long id);
}
