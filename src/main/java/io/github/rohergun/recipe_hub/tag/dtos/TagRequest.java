package io.github.rohergun.recipe_hub.tag.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequest(
        @NotBlank
        @Size(min = 3, max = 20)
        String name
) {
}
