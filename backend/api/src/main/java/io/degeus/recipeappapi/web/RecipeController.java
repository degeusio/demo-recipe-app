package io.degeus.recipeappapi.web;

import io.degeus.recipeappapi.config.PersistenceConfig;
import io.degeus.recipeappapi.domain.Recipe;
import io.degeus.recipeappapi.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = RecipeController.REQUEST_MAPPING)
@RequiredArgsConstructor
@Slf4j
@Transactional(transactionManager = PersistenceConfig.APP_TRANSACTION_MANAGER, propagation = Propagation.NOT_SUPPORTED) //as per design, no tx context in this layer
public class RecipeController {

    public static final String REQUEST_MAPPING = "/recipes";
    private final RecipeService recipeService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recipe> create(@RequestBody Recipe candidate,
                                         UriComponentsBuilder ucb) {

        Recipe created = recipeService.create(candidate);
        UriComponents uriComponents = ucb.path(REQUEST_MAPPING + "/{id}")
                .buildAndExpand(created.getId().toString());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @GetMapping(value = "/{recipe_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recipe> get(@PathVariable("recipe_id") String recipeId) {
        Recipe found = recipeService.findById(UUID.fromString(recipeId));
        return ResponseEntity.ok(found);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Recipe> getAll() {
        return recipeService.findAll();
    }

}
