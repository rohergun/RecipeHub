package io.github.rohergun.recipe_hub.recipe;

import io.github.rohergun.recipe_hub.recipe.dtos.CreateRecipeRequest;
import io.github.rohergun.recipe_hub.recipe.dtos.RecipeResponse;
import io.github.rohergun.recipe_hub.recipe.dtos.UpdateRecipeRequest;
import io.github.rohergun.recipe_hub.user.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    @GetMapping("/search")
    public ResponseEntity<Page<RecipeResponse>> searchByName(
            @RequestParam String recipeName,
            @PageableDefault(size = 8, sort = {"name"}, direction = Sort.Direction.ASC)Pageable pageable) {
        return ResponseEntity.ok().body(recipeService.getRecipesByName(recipeName, pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<RecipeResponse>> getAllByUser(
            @AuthenticationPrincipal AppUser user,
            @PageableDefault(size = 8, sort = {"createdAt"}, direction = Sort.Direction.DESC)Pageable pageable){
        return ResponseEntity.ok().body(recipeService.getUserRecipes(user.getId(), pageable));
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeResponse> getById(@PathVariable UUID recipeId){
        return ResponseEntity.ok().body(recipeService.getById(recipeId));
    }

    @PostMapping("/me")
    public ResponseEntity<RecipeResponse> createRecipe(
            @AuthenticationPrincipal AppUser user,
            @RequestBody @Valid CreateRecipeRequest request) {

        RecipeResponse created = recipeService.addRecipe(user.getId(), request);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/me/{recipeId}")
    public ResponseEntity<RecipeResponse> updateRecipe(
            @AuthenticationPrincipal AppUser user,
            @PathVariable UUID recipeId,
            @RequestBody @Valid UpdateRecipeRequest request){
       return ResponseEntity.ok().body(recipeService.updateRecipe(user.getId(), recipeId, request));
    }

    @DeleteMapping("/me/{recipeId}")
    public ResponseEntity<Void> deleteRecipe(
            @AuthenticationPrincipal AppUser user,
            @PathVariable UUID recipeId){

        recipeService.deleteRecipe(user.getId(), recipeId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{recipeId}/fork")
    public ResponseEntity<RecipeResponse> forkRecipe(
            @AuthenticationPrincipal AppUser user,
            @PathVariable UUID recipeId){
        RecipeResponse forked = recipeService.forkRecipe(user.getId(), recipeId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/recipes/{recipeId}")
                .buildAndExpand(forked.id())
                .toUri();

        return ResponseEntity.created(location).body(forked);
    }
}
