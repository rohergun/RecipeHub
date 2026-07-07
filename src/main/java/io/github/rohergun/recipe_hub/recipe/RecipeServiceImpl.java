package io.github.rohergun.recipe_hub.recipe;

import io.github.rohergun.recipe_hub.exception.DomainErrorMessage;
import io.github.rohergun.recipe_hub.exception.RecipeHubException;
import io.github.rohergun.recipe_hub.recipe.dtos.CreateRecipeRequest;
import io.github.rohergun.recipe_hub.recipe.dtos.RecipeResponse;
import io.github.rohergun.recipe_hub.recipe.dtos.UpdateRecipeRequest;
import io.github.rohergun.recipe_hub.user.AppUser;
import io.github.rohergun.recipe_hub.user.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
                .orElseThrow(() -> new RecipeHubException(DomainErrorMessage.RECIPE_NOT_FOUND));
        return recipeMapper.toResponse(recipe);
    }

    @Override
    @Transactional
    public RecipeResponse updateRecipe(UUID userId, UUID recipeId, UpdateRecipeRequest request) {
        Recipe recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new RecipeHubException(DomainErrorMessage.RECIPE_NOT_FOUND));

        if (!recipe.getCreatedBy().getId().equals(userId)) {
            throw new RecipeHubException(DomainErrorMessage.ACCESS_DENIED);
        }
        recipeMapper.updateRecipeFromRequest(request, recipe);
        recipeRepo.save(recipe);

        return recipeMapper.toResponse(recipe);
    }

    @Override
    @Transactional
    public RecipeResponse addRecipe(UUID userId, CreateRecipeRequest request) {
        AppUser user = userRepo.findById(userId)
                .orElseThrow(() -> new RecipeHubException(DomainErrorMessage.USER_NOT_FOUND));

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
                .orElseThrow(() -> new RecipeHubException(DomainErrorMessage.RECIPE_NOT_FOUND));

        if (!recipe.getCreatedBy().getId().equals(userId)){
            throw new RecipeHubException(DomainErrorMessage.ACCESS_DENIED);
        }
        recipeRepo.delete(recipe);
    }

    @Override
    @Transactional
    public RecipeResponse forkRecipe(UUID userId, UUID recipeId) {
        Recipe recipe = recipeRepo.findById(recipeId)
                .orElseThrow(() -> new RecipeHubException(DomainErrorMessage.RECIPE_NOT_FOUND));
        AppUser curUser = userRepo.getReferenceById(userId);

        if (recipe.getCreatedBy().getId().equals(userId)) {
            throw new RecipeHubException(DomainErrorMessage.CANNOT_FORK_OWN_RECIPE);
        }

        Recipe forkedRecipe = new Recipe();
        forkedRecipe.setName(recipe.getName());
        forkedRecipe.setDescription(recipe.getDescription());
        forkedRecipe.setTags(new ArrayList<>(recipe.getTags()));
        forkedRecipe.setIngredients(recipe.getIngredients());

        forkedRecipe.setCreatedBy(curUser);
        forkedRecipe.setForkedFrom(recipe);

        recipeRepo.save(forkedRecipe);
        return recipeMapper.toResponse(forkedRecipe);
    }
}
