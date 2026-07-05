package io.github.rohergun.recipe_hub.recipe;

import io.github.rohergun.recipe_hub.recipe.dtos.RecipeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping
    public ResponseEntity<Page<RecipeResponse>> getAll(
            @PageableDefault(size = 8, sort = {"numberOfLikes"}, direction = Sort.Direction.DESC)Pageable pageable) {
        return ResponseEntity.ok().body(recipeService.getAll(pageable));
    }

    @GetMapping("/{recipeName}")
    public ResponseEntity<Page<RecipeResponse>> getAllByName(
            @PathVariable String recipeName,
            @PageableDefault(size = 8, sort = {"name"}, direction = Sort.Direction.DESC)Pageable pageable) {
        return ResponseEntity.ok().body(recipeService.getRecipesByName(recipeName, pageable));
    }
}
