package io.github.rohergun.recipe_hub.recipe;

import io.github.rohergun.recipe_hub.recipe.dtos.CreateRecipeRequest;
import io.github.rohergun.recipe_hub.recipe.dtos.RecipeResponse;
import io.github.rohergun.recipe_hub.recipe.dtos.UpdateRecipeRequest;
import io.github.rohergun.recipe_hub.user.AppUser;
import io.github.rohergun.recipe_hub.user.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {

    @Mock
    private RecipeRepository recipeRepo;

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private AppUserRepository userRepo;

    @InjectMocks
    private RecipeServiceImpl recipeService;

    private AppUser owner;
    private AppUser otherUser;
    private UUID ownerId;
    private UUID otherUserId;
    private UUID recipeId;
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        recipeId = UUID.randomUUID();

        owner = new AppUser();
        ReflectionTestUtils.setField(owner, "id", ownerId);
        owner.setUsername("rohergun");

        otherUser = new AppUser();
        ReflectionTestUtils.setField(otherUser, "id", otherUserId);

        recipe = new Recipe();
        ReflectionTestUtils.setField(recipe, "id", recipeId);
        recipe.setName("Pasta Carbonara");
        recipe.setCreatedBy(owner);
        recipe.setNumberOfLikes(0);
    }

    private RecipeResponse sampleResponse() {
        return new RecipeResponse(
                recipeId, "Pasta Carbonara", "Classic Italian dish",
                List.of("eggs", "pasta", "bacon"), 0, owner
        );
    }

    // ---------- getById ----------

    @Test
    void getById_returnsRecipe_whenExists() {
        RecipeResponse expected = sampleResponse();

        when(recipeRepo.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(recipeMapper.toResponse(recipe)).thenReturn(expected);

        RecipeResponse result = recipeService.getById(recipeId);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getById_throws_whenNotFound() {
        when(recipeRepo.findById(recipeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.getById(recipeId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Recipe not found");
    }

    // ---------- getAll / getRecipesByName / getUserRecipes ----------

    @Test
    void getAll_returnsPagedResponses() {
        Pageable pageable = Pageable.ofSize(20);
        Page<Recipe> page = new PageImpl<>(List.of(recipe));

        when(recipeRepo.findAll(pageable)).thenReturn(page);
        when(recipeMapper.toResponse(recipe)).thenReturn(sampleResponse());

        Page<RecipeResponse> result = recipeService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getRecipesByName_delegatesToContainingIgnoreCaseQuery() {
        Pageable pageable = Pageable.ofSize(20);
        Page<Recipe> page = new PageImpl<>(List.of(recipe));

        when(recipeRepo.findAllByNameContainingIgnoreCase("pasta", pageable)).thenReturn(page);
        when(recipeMapper.toResponse(any(Recipe.class))).thenReturn(sampleResponse());

        Page<RecipeResponse> result = recipeService.getRecipesByName("pasta", pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(recipeRepo).findAllByNameContainingIgnoreCase("pasta", pageable);
    }

    @Test
    void getUserRecipes_delegatesToFindAllByCreatedById() {
        Pageable pageable = Pageable.ofSize(20);
        Page<Recipe> page = new PageImpl<>(List.of(recipe));

        when(recipeRepo.findAllByCreatedById(ownerId, pageable)).thenReturn(page);
        when(recipeMapper.toResponse(any(Recipe.class))).thenReturn(sampleResponse());

        Page<RecipeResponse> result = recipeService.getUserRecipes(ownerId, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(recipeRepo).findAllByCreatedById(ownerId, pageable);
    }

    // ---------- addRecipe ----------

    @Test
    void addRecipe_createsRecipe_whenUserExists() {
        CreateRecipeRequest request = new CreateRecipeRequest(
                "Pasta Carbonara", "Classic Italian dish", List.of("eggs", "pasta", "bacon")
        );
        RecipeResponse expected = sampleResponse();

        when(userRepo.findById(ownerId)).thenReturn(Optional.of(owner));
        when(recipeMapper.toResponse(any(Recipe.class))).thenReturn(expected);

        RecipeResponse result = recipeService.addRecipe(ownerId, request);

        assertThat(result).isEqualTo(expected);

        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepo).save(captor.capture());

        Recipe saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Pasta Carbonara");
        assertThat(saved.getDescription()).isEqualTo("Classic Italian dish");
        assertThat(saved.getIngredients()).containsExactly("eggs", "pasta", "bacon");
        assertThat(saved.getCreatedBy()).isEqualTo(owner);
        assertThat(saved.getNumberOfLikes()).isEqualTo(0);
    }

    @Test
    void addRecipe_throws_whenUserNotFound() {
        CreateRecipeRequest request = new CreateRecipeRequest(
                "Pasta Carbonara", "desc", List.of("eggs")
        );
        when(userRepo.findById(ownerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.addRecipe(ownerId, request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("User not found");

        verify(recipeRepo, never()).save(any());
    }

    // ---------- updateRecipe ----------

    @Test
    void updateRecipe_updatesAndSaves_whenUserIsOwner() {
        UpdateRecipeRequest request = new UpdateRecipeRequest("Updated Pasta", null, null);
        RecipeResponse expected = sampleResponse();

        when(recipeRepo.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(recipeMapper.toResponse(recipe)).thenReturn(expected);

        RecipeResponse result = recipeService.updateRecipe(ownerId, recipeId, request);

        assertThat(result).isEqualTo(expected);
        verify(recipeMapper).updateRecipeFromRequest(request, recipe);
        verify(recipeRepo).save(recipe);
    }

    @Test
    void updateRecipe_throwsAccessDenied_whenUserIsNotOwner() {
        UpdateRecipeRequest request = new UpdateRecipeRequest("Hacked Title", null, null);

        when(recipeRepo.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertThatThrownBy(() -> recipeService.updateRecipe(otherUserId, recipeId, request))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("permission");

        verify(recipeRepo, never()).save(any());
        verify(recipeMapper, never()).updateRecipeFromRequest(any(), any());
    }

    @Test
    void updateRecipe_throws_whenRecipeNotFound() {
        UpdateRecipeRequest request = new UpdateRecipeRequest("New Name", null, null);
        when(recipeRepo.findById(recipeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.updateRecipe(ownerId, recipeId, request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Recipe not found");
    }

    // ---------- deleteRecipe ----------

    @Test
    void deleteRecipe_deletes_whenUserIsOwner() {
        when(recipeRepo.findById(recipeId)).thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(ownerId, recipeId);

        verify(recipeRepo).delete(recipe);
    }

    @Test
    void deleteRecipe_throwsAccessDenied_whenUserIsNotOwner() {
        when(recipeRepo.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertThatThrownBy(() -> recipeService.deleteRecipe(otherUserId, recipeId))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("permission");

        verify(recipeRepo, never()).delete(any());
    }

    @Test
    void deleteRecipe_throws_whenRecipeNotFound() {
        when(recipeRepo.findById(recipeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.deleteRecipe(ownerId, recipeId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepo, never()).delete(any());
    }

    @Test
    void forkRecipe_createsForkedRecipe_whenUserIsNotOwner() {
        RecipeResponse expected = sampleResponse();

        when(recipeRepo.findById(recipeId)).thenReturn(Optional.of(recipe));
        when(userRepo.getReferenceById(otherUserId)).thenReturn(otherUser);
        when(recipeMapper.toResponse(any(Recipe.class))).thenReturn(expected);

        RecipeResponse result = recipeService.forkRecipe(otherUserId, recipeId);

        assertThat(result).isEqualTo(expected);

        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepo).save(captor.capture());

        Recipe saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo(recipe.getName());
        assertThat(saved.getDescription()).isEqualTo(recipe.getDescription());
        assertThat(saved.getIngredients()).isEqualTo(recipe.getIngredients());
        assertThat(saved.getCreatedBy()).isEqualTo(otherUser);
        assertThat(saved.getForkedFrom()).isEqualTo(recipe);
    }

    @Test
    void forkRecipe_throws_whenUserIsOwner() {
        when(recipeRepo.findById(recipeId)).thenReturn(Optional.of(recipe));

        assertThatThrownBy(() -> recipeService.forkRecipe(ownerId, recipeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot fork your own");

        verify(recipeRepo, never()).save(any());
    }

    @Test
    void forkRecipe_throws_whenRecipeNotFound() {
        when(recipeRepo.findById(recipeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recipeService.forkRecipe(otherUserId, recipeId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Recipe not found");

        verify(recipeRepo, never()).save(any());
    }
}