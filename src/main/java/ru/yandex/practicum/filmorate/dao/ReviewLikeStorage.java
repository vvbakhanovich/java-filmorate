package ru.yandex.practicum.filmorate.dao;

public interface ReviewLikeStorage {
    void add(long reviewId, long userId, String type);

    void delete(long reviewId, long userId, String type);
}
