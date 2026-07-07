package io.github.rohergun.recipe_hub.exception;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ApiError(DomainErrorMessage code, String message, LocalDateTime timestamp)
        implements Serializable { }
