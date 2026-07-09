package io.github.rohergun.recipe_hub.auth.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
        @Email @NotBlank
        String email,

        @Size(min = 2, max = 20, message = "Username length must be between {min} and {max} characters.")
        String username,
        @NotBlank @Size(min = 6, max = 50)
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{6,50}$",
                message = "Password must contain uppercase, lowercase, digit, and special character."
        )
        String password
){ }
