package io.github.rohergun.recipe_hub.recipe;

import io.github.rohergun.recipe_hub.recipe.dtos.CreateRecipeRequest;
import io.github.rohergun.recipe_hub.recipe.dtos.RecipeRespond;
import io.github.rohergun.recipe_hub.recipe.dtos.UpdateRecipeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RecipeService {
    Page<RecipeRespond> getRecipesByName(String name, Pageable pageable);
    Page<List<RecipeRespond>> getUserRecipes(UUID userId, Pageable pageable);
    Page<RecipeRespond> getAll(Pageable pageable);
    RecipeRespond getById(UUID recipeId);
    RecipeRespond updateRecipe(UUID userId, UUID recipeId, UpdateRecipeRequest request);
    RecipeRespond addRecipe(UUID userId, CreateRecipeRequest request);
    void deleteRecipe(UUID userId, UUID recipeId);
}
