package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.FeedDto;
import ru.yandex.practicum.filmorate.model.Feed;

@UtilityClass
public class FeedMapper {
    public static FeedDto toDto(Feed feed) {
        FeedDto feedDto = FeedDto.builder()
                .entityId(feed.getEntityId())
                .eventType(feed.getEventType())
                .eventId(feed.getEventId())
                .operation(feed.getOperation())
                .userId(feed.getUserId())
                .timestamp(feed.getTimestamp())
                .build();
        return feedDto;
    }

    public static Feed toModel(FeedDto feedDto) {
        Feed feed = Feed.builder()
                .entityId(feedDto.getEntityId())
                .eventType(feedDto.getEventType())
                .eventId(feedDto.getEventId())
                .operation(feedDto.getOperation())
                .userId(feedDto.getUserId())
                .timestamp(feedDto.getTimestamp())
                .build();
        return feed;
    }
}
