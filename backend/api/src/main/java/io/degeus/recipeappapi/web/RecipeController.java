package io.degeus.recipeappapi.web;

import io.degeus.recipeappapi.config.PersistenceConfig;
import io.degeus.recipeappapi.domain.Recipe;
import io.degeus.recipeappapi.service.RecipeService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.UUID;

@Api(tags = "Recipe operations",
        description = " " //overrides default name of 'Health Controller' if left empty
)
@RestController
@RequestMapping(path = RecipeController.REQUEST_MAPPING)
@RequiredArgsConstructor
@Slf4j
@Transactional(transactionManager = PersistenceConfig.APP_TRANSACTION_MANAGER, propagation = Propagation.NOT_SUPPORTED) //as per design, no tx context in this layer
public class RecipeController {

    public static final String REQUEST_MAPPING = "/recipes";
    private final RecipeService recipeService;

    @ApiOperation("Create a Recipe")
    @ApiResponses(value = {@ApiResponse(code = 201, message = "Created", response = Recipe.class)})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recipe> create(@ApiParam("The candidate Recipe") @RequestBody Recipe candidate,
                                         @ApiIgnore @ApiParam(hidden = true) UriComponentsBuilder ucb) {

        Recipe created = recipeService.create(candidate);
        UriComponents uriComponents = ucb.path(REQUEST_MAPPING + "/{id}")
                .buildAndExpand(created.getId().toString());
        return ResponseEntity.created(uriComponents.toUri()).build();
    }

    @ApiOperation("Get a Recipe")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Recipe.class)})
    @GetMapping(value = "/{recipe_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Recipe get(
            @ApiParam("The Recipe's id") @PathVariable("recipe_id") String recipeId) {
        return recipeService.findById(UUID.fromString(recipeId));
    }

    @ApiOperation("List all Recipes")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Recipe[].class)})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Recipe> getAll() {
        return recipeService.findAll();
    }

    @ApiOperation("Update a Recipe")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Recipe.class)})
    @PutMapping(value = "/{recipe_id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Recipe update(
            @ApiParam("The Recipe's id") @PathVariable("recipe_id") String recipeId,
            @ApiParam("The Recipe's new contents") @RequestBody Recipe candidate) {
        return recipeService.update(UUID.fromString(recipeId), candidate);
    }

    @ApiOperation("Delete a Recipe")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK")})
    @DeleteMapping(value = "/{recipe_id}")
    public ResponseEntity delete(
            @ApiParam("The Recipe's id") @PathVariable("recipe_id") String recipeId) {
        recipeService.delete(UUID.fromString(recipeId));
        return ResponseEntity.ok().build();
    }

}
