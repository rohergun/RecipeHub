package io.github.rohergun.recipe_hub.recipe.dtos;

import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateRecipeRequest (
        @Size(min = 2, max = 100)
        String name,
        @Size(max = 500)
        String description,
        List<String> ingredients
){
}
