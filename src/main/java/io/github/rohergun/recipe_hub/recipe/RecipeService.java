package io.github.rohergun.recipe_hub.recipe;

import io.github.rohergun.recipe_hub.recipe.dtos.CreateRecipeRequest;
import io.github.rohergun.recipe_hub.recipe.dtos.RecipeResponse;
import io.github.rohergun.recipe_hub.recipe.dtos.UpdateRecipeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RecipeService {
    Page<RecipeResponse> getRecipesByName(String name, Pageable pageable);
    Page<RecipeResponse> getUserRecipes(UUID userId, Pageable pageable);
    Page<RecipeResponse> getAll(Pageable pageable);
    RecipeResponse getById(UUID recipeId);
    RecipeResponse updateRecipe(UUID userId, UUID recipeId, UpdateRecipeRequest request);
    RecipeResponse addRecipe(UUID userId, CreateRecipeRequest request);
    void deleteRecipe(UUID userId, UUID recipeId);
}
