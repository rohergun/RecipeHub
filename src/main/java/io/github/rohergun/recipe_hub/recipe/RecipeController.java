package io.github.rohergun.recipe_hub.recipe;

import io.github.rohergun.recipe_hub.recipe.dtos.CreateRecipeRequest;
import io.github.rohergun.recipe_hub.recipe.dtos.RecipeResponse;
import io.github.rohergun.recipe_hub.recipe.dtos.UpdateRecipeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("/me")
    public ResponseEntity<Page<RecipeResponse>> getAllByUser(
            @AuthenticationPrincipal UUID userId,
            @PageableDefault(size = 8, sort = {"createdAt"}, direction = Sort.Direction.DESC)Pageable pageable){
        return ResponseEntity.ok().body(recipeService.getUserRecipes(userId, pageable));
    }

    @PostMapping("/me")
    public ResponseEntity<RecipeResponse> createRecipe(
            @AuthenticationPrincipal UUID userId,
            @RequestBody @Valid CreateRecipeRequest request) {

        RecipeResponse created = recipeService.addRecipe(userId, request);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/me/{recipeId}")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID recipeId,
            @RequestBody @Valid UpdateRecipeRequest request){
       return ResponseEntity.ok().body(recipeService.updateRecipe(userId, recipeId, request));
    }
}
