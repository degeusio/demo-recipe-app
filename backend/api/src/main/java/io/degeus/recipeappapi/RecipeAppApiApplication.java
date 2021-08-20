package io.degeus.recipeappapi;

import io.degeus.recipeappapi.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class RecipeAppApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecipeAppApiApplication.class, args);
	}

}
