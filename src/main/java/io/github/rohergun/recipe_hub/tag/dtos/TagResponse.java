package io.github.rohergun.recipe_hub.tag.dtos;

import java.time.LocalDateTime;

public record TagResponse (
        String name,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}
