package io.github.rohergun.recipe_hub.tag;

import io.github.rohergun.recipe_hub.tag.dtos.TagRequest;
import io.github.rohergun.recipe_hub.tag.dtos.TagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TagService {
    Page<TagResponse> listAllTags(Pageable pageable);
    Page<TagResponse> listTagsByUser(UUID userId, Pageable pageable);
    Page<TagResponse> listTagsByRecipe(UUID recipeId, Pageable pageable);
    TagResponse addTag(UUID userId, TagRequest request);
    TagResponse updateTag(UUID userId, UUID tagId, TagRequest request);
    void deleteTag(UUID userId, UUID tagId);
}
