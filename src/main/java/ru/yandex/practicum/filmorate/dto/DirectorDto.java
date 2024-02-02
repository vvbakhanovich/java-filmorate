package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DirectorDto {

    long id;
    @NotBlank(message = "Имя режиссера не может быть пустым.")
    String name;
}
