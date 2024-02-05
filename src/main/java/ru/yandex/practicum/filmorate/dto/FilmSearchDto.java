package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilmSearchDto {
    @Size(min = 1, message = "Нужно указать хотя бы 1 поле для поиска")
    @Size(max = 2, message = "Нужно указать не более 2 полей для поиска")
    @NotNull(message = "Нужно указать хотя бы 1 поле для поиска")
    @NotEmpty(message = "Нужно указать хотя бы 1 поле для поиска")
    private List<String> by;

    @NotNull(message = "Необходимо указать текст для поиска")
    private String query;

    public List<String> getBy() {
        return by.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }
}
