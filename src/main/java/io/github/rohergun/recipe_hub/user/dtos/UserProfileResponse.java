package io.github.rohergun.recipe_hub.user.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        String name,
        String bio,
        LocalDateTime createdAt
) {
}
