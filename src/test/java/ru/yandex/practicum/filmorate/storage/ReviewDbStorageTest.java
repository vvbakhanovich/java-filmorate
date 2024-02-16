package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.dao.impl.*;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReviewDbStorageTest {

    private final JdbcTemplate jdbcTemplate;

    private ReviewStorage reviewStorage;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private Review review1;
    private Review review2;
    private Review review3;
    private Review updatedReview;
    private Film film;
    private User user;

    @BeforeEach
    public void setUp() {
        reviewStorage = new ReviewDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
        Mpa mpa = new Mpa(1, "G");

        film = Film.builder()
                .id(1)
                .name("film")
                .description("film description")
                .releaseDate(LocalDate.of(2020, 12, 12))
                .duration(123)
                .mpa(mpa)
                .build();

        user = User.builder()
                .id(1)
                .email("email")
                .login("login")
                .name("name")
                .birthday(LocalDate.now())
                .build();
        filmStorage.add(film);
        userStorage.add(user);

        review1 = createReview(1);
        review2 = createReview(2);
        review3 = createReview(3);
        updatedReview = Review.builder()
                .reviewId(1)
                .content("updated review 1")
                .isPositive(true)
                .useful(13)
                .userId(4)
                .filmId(4)
                .build();
    }

    @Test
    @DisplayName("Тест добавления отзыва и получения по id.")
    public void addAndGetByIdTest() {
        reviewStorage.add(review1);

        Review savedReview = reviewStorage.findById(1);

        assertThat(savedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(review1);
    }

    @Test
    @DisplayName("Тест получения всех отзывов (сортировка по полезности).")
    public void getAllReviews() {
        reviewStorage.add(review1);
        reviewStorage.add(review2);
        reviewStorage.add(review3);
        Collection<Review> reviews = reviewStorage.findAll();

        assertThat(reviews)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(review3, review2, review1));
    }

    @Test
    @DisplayName("Тест получения всех отзывов без оценок полезности.")
    public void getAllReviewsWithZeroUseful() {
        review1.setUseful(0);
        review2.setUseful(0);
        review3.setUseful(0);
        reviewStorage.add(review1);
        reviewStorage.add(review2);
        reviewStorage.add(review3);
        Collection<Review> reviews = reviewStorage.findAll();

        assertThat(reviews)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(review1, review2, review3));
    }

    @Test
    @DisplayName("Тест получения всех отзывов при пустой базе данных.")
    public void getAllReviewsEmptyDb() {
        Collection<Review> reviews = reviewStorage.findAll();

        assertThat(reviews)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест получения отзывов с ограничением по количеству.")
    public void getAllReviewsLimitBy() {
        reviewStorage.add(review1);
        reviewStorage.add(review2);
        reviewStorage.add(review3);
        Collection<Review> reviews = reviewStorage.findAllLimitBy(2);

        assertThat(reviews)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(review3, review2));
    }

    @Test
    @DisplayName("Тест получения отзывов с ограничением по количеству.")
    public void getAllReviewsLimitBy10() {
        reviewStorage.add(review1);
        reviewStorage.add(review2);
        reviewStorage.add(review3);
        Collection<Review> reviews = reviewStorage.findAllLimitBy(10);

        assertThat(reviews)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(review3, review2, review1));
    }


    @Test
    @DisplayName("Тест получения отзывов по фильму с ограничением по количеству.")
    public void getAllReviewsFromFilmLimitBy() {
        reviewStorage.add(review1);
        reviewStorage.add(review2);
        reviewStorage.add(review3);
        Collection<Review> reviews = reviewStorage.findByFilmIdLimitBy(1, 1);

        assertThat(reviews)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(review3));
    }

    @Test
    @DisplayName("Тест получения отзывов по фильму с ограничением по количеству.")
    public void getAllReviewsFromFilmLimitBy10() {
        reviewStorage.add(review1);
        reviewStorage.add(review2);
        reviewStorage.add(review3);
        Collection<Review> reviews = reviewStorage.findByFilmIdLimitBy(1, 10);

        assertThat(reviews)
                .isNotNull()
                .isNotEmpty()
                .usingRecursiveComparison()
                .isEqualTo(List.of(review3, review2, review1));
    }

    @Test
    @DisplayName("Тест удаления отзыва.")
    public void deleteReview() {
        reviewStorage.add(review1);
        reviewStorage.remove(1);
        Collection<Review> reviews = reviewStorage.findAll();

        assertThat(reviews)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Тест обновления отзыва. Обновиться должны только поля content, isPositive.")
    public void updateReview() {
        reviewStorage.add(review1);
        reviewStorage.update(updatedReview);

        Review storedReview = reviewStorage.findById(1);

        assertThat(storedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .comparingOnlyFields("content", "isPositive")
                .isEqualTo(updatedReview);

        assertThat(storedReview)
                .isNotNull()
                .usingRecursiveComparison()
                .comparingOnlyFields("useful", "filmId", "userId")
                .isEqualTo(review1);
    }

    @Test
    @DisplayName("Тест добавления лайка отзыву.")
    public void addLikeToReview() {
        reviewStorage.add(review1);

        reviewStorage.addLikeOrDislikeToReview(1, 1, ReviewLike.LIKE.toString());
        Review storedReview = reviewStorage.findById(1);
        assertEquals(2, storedReview.getUseful());
    }

    @Test
    @DisplayName("Тест добавления лайка отзыву c отрицательным рейтингом.")
    public void addLikeToReviewNegativeUseful() {
        review1.setUseful(-1);
        reviewStorage.add(review1);

        reviewStorage.addLikeOrDislikeToReview(1, 1, ReviewLike.LIKE.toString());
        Review storedReview = reviewStorage.findById(1);
        assertEquals(0, storedReview.getUseful());
    }

    @Test
    @DisplayName("Тест добавления дизлайка отзыву.")
    public void addDislikeToReview() {
        reviewStorage.add(review1);

        reviewStorage.addLikeOrDislikeToReview(1, 1, ReviewLike.DISLIKE.toString());
        Review storedReview = reviewStorage.findById(1);
        assertEquals(0, storedReview.getUseful());
    }

    @Test
    @DisplayName("Тест добавления дизлайка отзыву c отрицательным рейтингом.")
    public void addDislikeToReviewNegativeUseful() {
        review1.setUseful(-1);
        reviewStorage.add(review1);

        reviewStorage.addLikeOrDislikeToReview(1, 1, ReviewLike.DISLIKE.toString());
        Review storedReview = reviewStorage.findById(1);
        assertEquals(-2, storedReview.getUseful());
    }

    private Review createReview(long id) {
        return Review.builder()
                .reviewId(id)
                .content("review " + id)
                .isPositive(true)
                .useful(id)
                .userId(1)
                .filmId(1)
                .build();
    }
}