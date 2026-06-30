package io.github.rohergun.recipe_hub.recipe.dtos;

import io.github.rohergun.recipe_hub.user.AppUser;

import java.util.List;
import java.util.UUID;

public record RecipeResponse(
        UUID id,
        String name,
        String description,
        List<String> ingredients,
        int numberOfLikes,
        AppUser createdBy
) {
}
