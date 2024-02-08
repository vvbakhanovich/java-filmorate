package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.mapper.ReviewMapper.toDto;
import static ru.yandex.practicum.filmorate.mapper.ReviewMapper.toModel;
import static ru.yandex.practicum.filmorate.model.ReviewLike.DISLIKE;
import static ru.yandex.practicum.filmorate.model.ReviewLike.LIKE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final ReviewLikeStorage reviewLikeStorage;
    private final EventStorage eventStorage;

    /**
     * Добавление отзыва в БД.
     *
     * @param reviewDto отзыв.
     * @return отзыв с присвоенным идентификатором.
     */
    @Override
    @Transactional
    public ReviewDto addReview(final ReviewDto reviewDto) {
        findUserAndFilmInDb(reviewDto);
        final Review review = toModel(reviewDto);
        final Review addedReview = reviewStorage.add(review);
        log.info("Добавлен новый отзыв: {}.", addedReview);
        eventStorage.addEvent(EventType.REVIEW.name(), Operation.ADD.name(), review.getReviewId(), review.getUserId());
        return toDto(reviewStorage.findById(addedReview.getReviewId()));
    }

    /**
     * Получение отзыва по идентификатору.
     *
     * @param id идентификатор отзыва.
     * @return найденный отзыв.
     */
    @Override
    public ReviewDto getReviewById(final long id) {
        final Review review = reviewStorage.findById(id);
        log.info("Найден отзыв с id '{}'.", id);
        return toDto(review);
    }

    /**
     * Обновление данных отзыва. Происходит обновление только полей content и isPositive.
     *
     * @param updatedReviewDto отзыв с обновленными полями.
     * @return обновленный отзыв.
     */
    @Override
    @Transactional
    public ReviewDto updateReview(final ReviewDto updatedReviewDto) {
        final long reviewId = updatedReviewDto.getReviewId();
        reviewStorage.findById(reviewId);
        final Review updatedReview = toModel(updatedReviewDto);
        reviewStorage.update(updatedReview);
        log.info("Обновление отзыва с id '{}': {}", reviewId, updatedReview);
        Review currentReview = reviewStorage.findById(reviewId);
        eventStorage.addEvent(EventType.REVIEW.name(), Operation.UPDATE.name(), currentReview.getReviewId(), currentReview.getUserId());
        return toDto(currentReview);
    }

    /**
     * Удаление отзыва из БД.
     *
     * @param id идентификатор отзыва.
     */
    @Override
    @Transactional
    public void deleteReview(final long id) {
        ReviewDto review = getReviewById(id);
        reviewStorage.remove(id);
        eventStorage.addEvent(EventType.REVIEW.name(), Operation.REMOVE.name(), id, review.getUserId());
        log.info("Отзыв с id '{}' был удален.", id);
    }

    /**
     * Получение списка отзывов о фильме. Если идентификатор фильма не был передан, то выводится список всех отзывов.
     *
     * @param filmId идентификатор фильма.
     * @param count  количество отзывов, которое требуется вывести. По умолчанию 10.
     * @return список отзывов.
     */
    @Override
    public List<ReviewDto> getReviewsByFilmId(final Long filmId, final int count) {
        if (filmId == null) {
            final List<Review> reviews = reviewStorage.findAllLimitBy(count);
            log.info("Запрос на получение отзывов.");
            return reviews.stream().map(ReviewMapper::toDto).collect(Collectors.toList());
        } else {
            filmStorage.findById(filmId);
            final List<Review> reviews = reviewStorage.findByFilmIdLimitBy(filmId, count);
            log.info("Запрос на получение отзывов по фильму с id '{}'.", filmId);
            return reviews.stream().map(ReviewMapper::toDto).collect(Collectors.toList());
        }
    }

    /**
     * Добавление лайка отзыву.
     *
     * @param id     идентификатор отзыва.
     * @param userId идентификатор пользователя, который ставит лайк.
     * @return отзыв с добавленным лайком.
     */
    @Override
    @Transactional
    public ReviewDto addLikeToReview(final long id, final long userId) {
        findReviewAndUserInDb(id, userId);
        reviewStorage.addLikeToReview(id);
        reviewLikeStorage.add(id, userId, LIKE.toString());
        log.info("Пользователь с id '{}' поставил лайк отзыву с id '{}'", userId, id);
        return toDto(reviewStorage.findById(id));
    }

    /**
     * Добавление дизлайка отзыву.
     *
     * @param id     идентификатор отзыва.
     * @param userId идентификатор пользователя, который ставит дизлайк.
     * @return отзыв с добавленным дизлайком.
     */
    @Override
    @Transactional
    public ReviewDto addDislikeToReview(long id, long userId) {
        findReviewAndUserInDb(id, userId);
        reviewStorage.addDislikeToReview(id);
        reviewLikeStorage.add(id, userId, DISLIKE.toString());
        log.info("Пользователь с id '{}' поставил дизлайк отзыву с id '{}'", userId, id);
        return toDto(reviewStorage.findById(id));
    }

    /**
     * Удаление лайка у отзыва.
     *
     * @param id     идентификатор отзыва.
     * @param userId идентификатор пользователя, который удаляет лайк.
     * @return отзыв с удаленным лайком.
     */
    @Override
    @Transactional
    public ReviewDto deleteLikeFromReview(long id, long userId) {
        findReviewAndUserInDb(id, userId);
        reviewStorage.addDislikeToReview(id);
        reviewLikeStorage.delete(id, userId, LIKE.toString());
        log.info("Пользователь с id '{}' удалил лайк отзыву с id '{}'", userId, id);
        return toDto(reviewStorage.findById(id));
    }

    /**
     * Удаление дизлайка у отзыва.
     *
     * @param id     идентификатор отзыва.
     * @param userId идентификатор пользователя, который удаляет дизлайк.
     * @return отзыв с удаленным дизлайком.
     */
    @Override
    @Transactional
    public ReviewDto deleteDislikeFromReview(long id, long userId) {
        findReviewAndUserInDb(id, userId);
        reviewStorage.addLikeToReview(id);
        reviewLikeStorage.delete(id, userId, DISLIKE.toString());
        log.info("Пользователь с id '{}' удалил дизлайк отзыву с id '{}'", userId, id);
        return toDto(reviewStorage.findById(id));
    }

    private void findReviewAndUserInDb(long id, long userId) {
        reviewStorage.findById(id);
        userStorage.findById(userId);
    }

    private void findUserAndFilmInDb(ReviewDto updatedReviewDto) {
        userStorage.findById(updatedReviewDto.getUserId());
        filmStorage.findById(updatedReviewDto.getFilmId());
    }
}
