package io.degeus.recipeappapi.repository;

import io.degeus.recipeappapi.config.PersistenceConfig;
import io.degeus.recipeappapi.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(transactionManager = PersistenceConfig.APP_TRANSACTION_MANAGER, propagation = Propagation.MANDATORY) //enforce tx context
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

}
