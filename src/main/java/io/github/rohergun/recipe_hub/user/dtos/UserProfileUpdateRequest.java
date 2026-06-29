package io.github.rohergun.recipe_hub.user.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfileUpdateRequest(
        @NotBlank
        @Size(min = 3, max = 50)
        String username,
        @Size(max = 500)
        String bio
) {
}
