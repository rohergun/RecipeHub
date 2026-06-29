package io.github.rohergun.recipe_hub.recipe.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateRecipeRequest (
        @NotBlank
        @Size(min = 2, max = 100)
        String name,
        @Size(max = 500)
        String description,
        @NotEmpty
        List<String> ingredients
){
}
