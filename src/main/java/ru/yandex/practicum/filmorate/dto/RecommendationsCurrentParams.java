package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendationsCurrentParams {

    private int currentDiff;
    private int currentNumberOfMatches;
    private int currentNumberOfLikedFilms;
}
