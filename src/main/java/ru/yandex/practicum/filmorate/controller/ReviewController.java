package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto addReview(@Valid @RequestBody ReviewDto reviewDto) {
        return reviewService.addReview(reviewDto);
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody ReviewDto updatedReviewDto) {
        return reviewService.updateReview(updatedReviewDto);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable long id) {
        reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<ReviewDto> getReviewsByFilmId(@RequestParam(required = false) Long filmId,
                                              @RequestParam(defaultValue = "10") int count) {
        return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ReviewDto addLikeToReview(@PathVariable long id, @PathVariable long userId) {
        return reviewService.addLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ReviewDto addDislikeToReview(@PathVariable long id, @PathVariable long userId) {
        return reviewService.addDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ReviewDto deleteLikeFromReview(@PathVariable long id, @PathVariable long userId) {
        return reviewService.deleteLikeFromReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ReviewDto deleteDislikeFromReview(@PathVariable long id, @PathVariable long userId) {
        return reviewService.deleteDislikeFromReview(id, userId);
    }
}
