package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.ReviewMapper.toDto;
import static ru.yandex.practicum.filmorate.mapper.ReviewMapper.toModel;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;


    @Override
    public ReviewDto addReview(final ReviewDto reviewDto) {
        findUserAndFilmInDb(reviewDto);
        final Review review = toModel(reviewDto);
        final Review addedReview = reviewStorage.add(review);
        log.info("Добавлен новый отзыв: {}.", addedReview);
        return toDto(reviewStorage.findById(addedReview.getReviewId()));
    }

    @Override
    public ReviewDto getReviewById(final long id) {
        final Review review = reviewStorage.findById(id);
        log.info("Найден отзыв с id '{}'.", id);
        return toDto(review);
    }

    @Override
    public ReviewDto updateReview(final ReviewDto updatedReviewDto) {
        findUserAndFilmInDb(updatedReviewDto);
        final Review updatedReview = toModel(updatedReviewDto);
        reviewStorage.update(updatedReview);
        final long reviewId = updatedReview.getReviewId();
        log.info("Обновление отзыва с id '{}': {}", reviewId, updatedReview);
        return toDto(reviewStorage.findById(reviewId));
    }

    @Override
    public void deleteReview(final long id) {
        reviewStorage.findById(id);
        reviewStorage.remove(id);
        log.info("Отзыв с id '{}' был удален.", id);
    }

    @Override
    public List<ReviewDto> getReviewsByFilmId(final long filmId, final int count) {
        filmStorage.findById(filmId);
        final List<Review> reviews = reviewStorage.findByFilmIdLimitBy(filmId, count);
        log.info("Запрос на получение отзывов по фильму с id '{}'.", filmId);
        return reviews.stream().map(ReviewMapper::toDto).collect(Collectors.toList());
    }

    private void findUserAndFilmInDb(ReviewDto updatedReviewDto) {
        userStorage.findById(updatedReviewDto.getUserId());
        filmStorage.findById(updatedReviewDto.getFilmId());
    }


}
