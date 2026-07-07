package io.github.rohergun.recipe_hub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecipeHubException.class)
    public ResponseEntity<ApiError> handleRecipeHubException(RecipeHubException ex) {
        ApiError error = new ApiError(
                ex.getErrorMessage(),
                ex.getErrorMessage().getDescription(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(ex.getErrorMessage().getHttpStatus()).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        ApiError error = new ApiError(
                DomainErrorMessage.ACCESS_DENIED,
                DomainErrorMessage.ACCESS_DENIED.getDescription(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        ApiError error = new ApiError(
                DomainErrorMessage.INTERNAL_SERVER_ERROR,
                DomainErrorMessage.INTERNAL_SERVER_ERROR.getDescription(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
