package io.github.rohergun.recipe_hub.recipe.dtos;

import java.util.List;
import java.util.UUID;

public record RecipeRespond(
        UUID id,
        String name,
        String description,
        List<String> ingredients,
        int numberOfLikes
) {
}
