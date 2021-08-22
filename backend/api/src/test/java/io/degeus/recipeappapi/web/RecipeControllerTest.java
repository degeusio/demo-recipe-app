package io.degeus.recipeappapi.web;

import io.degeus.recipeappapi.domain.Recipe;
import io.degeus.recipeappapi.service.RecipeService;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    RecipeService mockRecipeService;

    @InjectMocks
    RecipeController testVictim;

    @Test
    void itShouldDelegateCreationToServiceLayer() {

        //Arrange
        Recipe mockRecipe = mock(Recipe.class);
        UUID uuid = UUID.randomUUID();
        when(mockRecipe.getId()).thenReturn(uuid);
        UriComponentsBuilder ucb = UriComponentsBuilder.newInstance();
        doReturn(mockRecipe).when(mockRecipeService).create(eq(mockRecipe));

        //Act
        ResponseEntity<Recipe> response = testVictim.create(mockRecipe, ucb);

        //Assess
        MatcherAssert.assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    void itShouldGetRecipeFromServiceLayer() {

        //Arrange
        UUID uuid = UUID.randomUUID();
        Recipe mockRecipe = mock(Recipe.class);
        when(mockRecipe.getId()).thenReturn(uuid);
        doReturn(mockRecipe).when(mockRecipeService).findById(eq(uuid));

        //Act
        Recipe recipe = testVictim.get(uuid.toString());

        //Assess
        MatcherAssert.assertThat(recipe.getId(), is(uuid));

    }

    @Test
    void itShouldListRecipesFromServiceLayer() {

        //Arrange
        UUID uuid = UUID.randomUUID();
        Recipe mockRecipe = mock(Recipe.class);
        when(mockRecipe.getId()).thenReturn(uuid);
        doReturn(List.of(mockRecipe)).when(mockRecipeService).findAll();

        //Act
        List<Recipe> recipes = testVictim.getAll();

        //Assess
        MatcherAssert.assertThat(recipes.size(), is(1));
        MatcherAssert.assertThat(recipes.get(0).getId(), is(uuid));
    }

    @Test
    void itShouldUpdateRecipeWithServiceLayer() {

        //Arrange
        UUID uuid = UUID.randomUUID();
        Recipe mockRecipe = mock(Recipe.class);
        when(mockRecipe.getId()).thenReturn(uuid);
        doReturn(mockRecipe).when(mockRecipeService).update(eq(uuid), eq(mockRecipe));

        //Act
        Recipe updated = testVictim.update(uuid.toString(), mockRecipe);

        //Assess
        MatcherAssert.assertThat(updated.getId(), is(uuid));
    }

    @Test
    void itShouldDeleteRecipeWithServiceLayer() {

        //Arrange
        UUID uuid = UUID.randomUUID();

        //Act
        testVictim.delete(uuid.toString());

        //Assess
        verify(mockRecipeService, times(1)).delete(eq(uuid));
    }
}
