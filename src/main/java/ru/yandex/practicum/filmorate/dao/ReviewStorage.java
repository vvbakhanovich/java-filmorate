package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage extends Dao<Review> {
    List<Review> findByFilmIdLimitBy(long filmId, int count);

    List<Review> findAllLimitBy(int count);

    void addLikeToReview(long reviewId, long userId, String type);

    void addDislikeToReview(long reviewId, long userId, String type);
}
