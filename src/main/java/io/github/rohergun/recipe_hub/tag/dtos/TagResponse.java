package io.github.rohergun.recipe_hub.tag.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record TagResponse (
        UUID id,
        String name,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}
