package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

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
        final Review review = toModel(reviewDto);
        userStorage.findById(review.getUserId());
        filmStorage.findById(review.getFilmId());
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


}
