package io.github.rohergun.recipe_hub.recipe;

import io.github.rohergun.recipe_hub.recipe.dtos.CreateRecipeRequest;
import io.github.rohergun.recipe_hub.recipe.dtos.RecipeResponse;
import io.github.rohergun.recipe_hub.recipe.dtos.UpdateRecipeRequest;
import io.github.rohergun.recipe_hub.user.AppUser;
import io.github.rohergun.recipe_hub.user.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService{
    private final RecipeRepository recipeRepo;
    private final RecipeMapper recipeMapper;
    private final AppUserRepository userRepo;

    @Override
    public Page<RecipeResponse> getRecipesByName(String name, Pageable pageable) {
        return recipeRepo.findAllByNameContainingIgnoreCase(name, pageable)
                .map(recipeMapper::toResponse);
    }

    @Override
    public Page<RecipeResponse> getUserRecipes(UUID userId, Pageable pageable) {
        return recipeRepo.findAllByCreatedById(userId, pageable).map(recipeMapper::toResponse);
    }

    @Override
    public Page<RecipeResponse> getAll(Pageable pageable) {

        return recipeRepo.findAll(pageable).map(recipeMapper::toResponse);
    }

    @Override
    public RecipeResponse getById(UUID recipeId) {
        Recipe recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));
        return recipeMapper.toResponse(recipe);
    }

    @Override
    @Transactional
    public RecipeResponse updateRecipe(UUID userId, UUID recipeId, UpdateRecipeRequest request) {
        Recipe recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));

        if (!recipe.getCreatedBy().getId().equals(userId)) {
            throw new AccessDeniedException("You dont have permission to update this recipe");
        }
        recipeMapper.updateRecipeFromRequest(request, recipe);
        recipeRepo.save(recipe);

        return recipeMapper.toResponse(recipe);
    }

    @Override
    @Transactional
    public RecipeResponse addRecipe(UUID userId, CreateRecipeRequest request) {
        AppUser user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        Recipe newRecipe = new Recipe();
        newRecipe.setName(request.name());
        newRecipe.setDescription(request.description());
        newRecipe.setIngredients(request.ingredients());

        newRecipe.setCreatedBy(user);
        newRecipe.setNumberOfLikes(0);

        recipeRepo.save(newRecipe);
        return recipeMapper.toResponse(newRecipe);
    }

    @Override
    @Transactional
    public void deleteRecipe(UUID userId, UUID recipeId) {
        Recipe recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));

        if (!recipe.getCreatedBy().getId().equals(userId)){
            throw new AccessDeniedException("You dont have permission to delete this recipe");
        }
        recipeRepo.delete(recipe);
    }

    @Override
    @Transactional
    public RecipeResponse forkRecipe(UUID userId, UUID recipeId) {
        Recipe recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));
        AppUser curUser = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (recipe.getCreatedBy().getId().equals(userId)) {
            throw  new IllegalArgumentException("You cannot fork your own recipe");
        }

        Recipe forkedRecipe = new Recipe();
        forkedRecipe.setName(recipe.getName());
        forkedRecipe.setDescription(recipe.getDescription());
        forkedRecipe.setTags(recipe.getTags());
        forkedRecipe.setIngredients(recipe.getIngredients());

        forkedRecipe.setCreatedBy(curUser);
        forkedRecipe.setForkedFrom(recipe);

        recipeRepo.save(forkedRecipe);
        return recipeMapper.toResponse(forkedRecipe);
    }
}
