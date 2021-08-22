package io.degeus.recipeappapi.web;

import io.degeus.recipeappapi.domain.Recipe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/recipes")
@Slf4j
public class RecipeController {

    @GetMapping
    public List<Recipe> getAll() {

        //use a mock for now
        Recipe one = new Recipe();
        one.setId(UUID.randomUUID());
        return List.of(one);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recipe> create(Recipe candidate) {

        return ResponseEntity.ok(null); //for now
    }
}
