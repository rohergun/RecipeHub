package io.github.rohergun.recipe_hub.tag;

import io.github.rohergun.recipe_hub.recipe.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    boolean existsByNameAndCreatedById(String name, UUID createdById);
    Page<Tag> findAllByCreatedById(UUID userId, Pageable pageable);
    Page<Tag> findAllByRecipes_Id(UUID recipeId, Pageable pageable);
}
