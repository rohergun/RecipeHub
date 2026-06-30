package io.github.rohergun.recipe_hub.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID>{
    Page<Recipe> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Recipe> findAllByCreatedById(UUID userId, Pageable pageable);
}
