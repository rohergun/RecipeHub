package io.github.rohergun.recipe_hub.exception;

import lombok.Getter;

@Getter
public class RecipeHubException extends RuntimeException{
    private final DomainErrorMessage errorMessage;

    public RecipeHubException(DomainErrorMessage errorMessage) {
        super(errorMessage.getDescription());
        this.errorMessage = errorMessage;
    }
}
