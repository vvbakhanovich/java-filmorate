package ru.yandex.practicum.filmorate.configuration;


import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.yandex.practicum.filmorate.storage.IdGenerator;
import ru.yandex.practicum.filmorate.storage.LongIdGenerator;
import ru.yandex.practicum.filmorate.util.Constants;

@Configuration
public class MyConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {

            builder.deserializers(new LocalDateDeserializer(Constants.DATE_FORMATTER));

            builder.serializers(new LocalDateSerializer(Constants.DATE_FORMATTER));
        };
    }
}
