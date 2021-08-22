package io.degeus.recipeappapi.web;

import io.degeus.recipeappapi.config.PersistenceConfig;
import io.degeus.recipeappapi.domain.Recipe;
import io.degeus.recipeappapi.service.RecipeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/recipes")
@RequiredArgsConstructor
@Slf4j
@Transactional(transactionManager = PersistenceConfig.APP_TRANSACTION_MANAGER, propagation = Propagation.NOT_SUPPORTED) //as per design, no tx context in this layer
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public List<Recipe> getAll() {

        return recipeService.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recipe> create(Recipe candidate, Authentication auth) {

        return ResponseEntity.ok(null); //for now
    }
}
