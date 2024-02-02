package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feed {
    private long eventId; //целочисленный идентификатор
    private EventType eventType; //тип события
    private Operation operation; //тип операции
    private Long timestamp; //дата релиза
    private long entityId; //идентификатор сущности, над которой было совершено действие
    private long userId; //идентификатор пользователя, который совершил действие
}
