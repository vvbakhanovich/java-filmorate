package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewLikeStorage;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewLikeDbStorage implements ReviewLikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(final long reviewId, final long userId, final String type) {
        final String sql = "INSERT INTO review_like VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, type);
    }

    @Override
    public void delete(final long reviewId, final long userId, final String type) {
        final String sql = "DELETE FROM review_like WHERE review_id = ? AND user_id = ? AND like_type = ?";
        jdbcTemplate.update(sql, userId, type);
    }
}
