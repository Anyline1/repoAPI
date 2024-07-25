package ru.anyline.repoapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        // Создание и настройка ObjectMapper
        ObjectMapper mapper = Jackson2ObjectMapperBuilder.json().build();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}

