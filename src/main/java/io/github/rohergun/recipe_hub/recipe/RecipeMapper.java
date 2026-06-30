package io.github.rohergun.recipe_hub.recipe;

import io.github.rohergun.recipe_hub.recipe.dtos.RecipeResponse;
import io.github.rohergun.recipe_hub.recipe.dtos.UpdateRecipeRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RecipeMapper {
    RecipeResponse toResponse(Recipe recipe);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRecipeFromRequest(UpdateRecipeRequest request, @MappingTarget Recipe recipe);
}
