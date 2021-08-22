package io.degeus.recipeappapi.service;

import io.degeus.recipeappapi.config.PersistenceConfig;
import io.degeus.recipeappapi.domain.Recipe;
import io.degeus.recipeappapi.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Component
@Validated //as per doc, enables @Valid JSR-303 validation support at (public) method level
@Transactional(transactionManager = PersistenceConfig.APP_TRANSACTION_MANAGER, propagation = Propagation.REQUIRED) //as per design, create or reuse tx context in service layer
@RequiredArgsConstructor
@Slf4j
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    public Recipe findById(UUID id) throws EntityNotFoundException {
        try {
            return recipeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        } catch (Exception ex) {
            log.error("Error with persistence layer. Error [{}].", ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    @PreAuthorize("isAuthenticated() and hasRole('ADMINISTRATOR')")
    public Recipe create(@Valid Recipe candidate) {
        if (candidate.getId() != null) {
            throw new IllegalArgumentException("Should not have an ID already - this is application generated!");
        }

        try {
            UUID uuid = UUID.randomUUID();
            candidate.setId(uuid);
            return recipeRepository.save(candidate);
        } catch (Exception ex) {
            log.error("Error persisting entity. Error:", ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    @PreAuthorize("#recipeId.equals(#candidate.id) and isAuthenticated() and hasRole('ADMINISTRATOR')")
    public Recipe update(UUID recipeId, @Valid Recipe candidate) {
        try {
            if (!recipeRepository.existsById(candidate.getId())) {
                throw new EntityNotFoundException();
            }
            return recipeRepository.save(candidate); //full overwrite
        } catch (Exception ex) {
            log.error("Error persisting entity. Error:", ex);
            throw new RuntimeException(ex.getMessage());
        }
    }
}
