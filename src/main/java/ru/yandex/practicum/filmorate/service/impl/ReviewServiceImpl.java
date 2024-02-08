package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dao.EventStorage;
import ru.yandex.practicum.filmorate.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dao.UserStorage;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventStorage eventStorage;

    /**
     * Добавление отзыва в БД.
     *
     * @param reviewDto отзыв.
     * @return отзыв с присвоенным идентификатором.
     */
    @Override
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
    @Transactional
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
     * Добавление лайка или дизлайка отзыву.
     *
     * @param id     идентификатор отзыва.
     * @param userId идентификатор пользователя, который ставит лайк/дизлайк.
     * @return отзыв с добавленным лайком/дизлайк.
     */
    @Override
    @Transactional
    public ReviewDto addLikeOrDislikeToReview(final long id, final long userId, String type) {
        findReviewAndUserInDb(id, userId);
        reviewStorage.addLikeOrDislikeToReview(id, userId, type);
        log.info("Пользователь с id '{}' поставил лайк отзыву с id '{}'", userId, id);
        return toDto(reviewStorage.findById(id));
    }

    /**
     * Удаление лайка или дизлайка у отзыва.
     *
     * @param id     идентификатор отзыва.
     * @param userId идентификатор пользователя, который удаляет лайк/дизлайк.
     * @return отзыв с удаленным лайком/дизлайк.
     */
    @Override
    @Transactional
    public ReviewDto deleteLikeOrDislikeFromReview(long id, long userId, String type) {
        findReviewAndUserInDb(id, userId);
        reviewStorage.deleteLikeOrDislikeFromReview(id, userId, type);
        log.info("Пользователь с id '{}' удалил лайк отзыву с id '{}'", userId, id);
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
