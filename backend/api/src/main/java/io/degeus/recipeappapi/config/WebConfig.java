package io.degeus.recipeappapi.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    /**
     * the {@link com.fasterxml.jackson.databind.ObjectMapper object mapper} that should be globally used.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // in principle, write dates as normal ISO8601 datetimes e.g.: {"statusTimestamp":"2017-08-14T12:17:47.720Z"}
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE); //writes 'someId' as 'some_id'
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); //do not force clients to pass explicit null values
        return objectMapper;
    }

}
