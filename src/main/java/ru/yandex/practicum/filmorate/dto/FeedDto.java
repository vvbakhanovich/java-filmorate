package ru.yandex.practicum.filmorate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedDto {
    private long eventId; //целочисленный идентификатор
    @NotBlank(message = "Тип события не может быть пустым.")
    private EventType eventType; //тип события
    @NotBlank(message = "Тип операции не может быть пустым.")
    private Operation operation; //тип операции
    private Long timestamp; //дата релиза
    @NotBlank(message = "Идентификатор сущности, которую изменили, не может быть пустым.")
    private long entityId; //идентификатор сущности, над которой было совершено действие
    @NotBlank(message = "Идентификатор пользователя не может быть пустым.")
    private long userId; //идентификатор пользователя, который совершил действие
}
