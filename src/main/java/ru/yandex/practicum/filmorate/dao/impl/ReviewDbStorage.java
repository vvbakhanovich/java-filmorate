package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review add(final Review review) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO review (review_content, is_positive, useful, user_id, film_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.isPositive());
            stmt.setLong(3, review.getUseful());
            stmt.setLong(4, review.getUserId());
            stmt.setLong(5, review.getFilmId());
            return stmt;
        }, keyHolder);

        review.setReviewId(Objects.requireNonNull(keyHolder.getKey(), "Не удалось добавить отзыв.").longValue());

        return review;
    }

    @Override
    public void remove(final long id) {

    }

    @Override
    public void update(final Review review) {
        final String sql = "UPDATE review SET review_content = ?, is_positive = ?, useful = ? " +
                "WHERE id = ?";
        final int update = jdbcTemplate.update(sql, review.getContent(), review.isPositive(), review.getUseful(),
                review.getReviewId());
        if (update != 1) {
            throw new NotFoundException("Отзыв с id '" + review.getReviewId() + "' не найден.");
        }
    }

    @Override
    public Collection<Review> findAll() {
        return null;
    }

    @Override
    public Review findById(final long id) {
        final String sql = "SELECT ID, REVIEW_CONTENT, IS_POSITIVE, USEFUL, USER_ID, FILM_ID " +
                "FROM REVIEW WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapReview, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Отзыв с id '" + id + "' не найден.");
        }
    }

    private Review mapReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("id"))
                .content(rs.getString("review_content"))
                .isPositive(rs.getBoolean("is_positive"))
                .useful(rs.getLong("useful"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .build();
    }
}
