package io.github.rohergun.recipe_hub.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public enum DomainErrorMessage {
    // USER DOMAIN
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "Username is already exists"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email is already exists"),

    // RECIPE DOMAIN
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "Recipe not found"),
    CANNOT_FORK_OWN_RECIPE(HttpStatus.BAD_REQUEST, "You cannot fork your own recipe"),

    // TAG DOMAIN
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "Tag not found"),
    TAG_ALREADY_EXISTS(HttpStatus.CONFLICT, "Tag with this name already exists"),

    // ACCESS DENIED
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "You dont have permission to access this resource"),

    // GENERIC
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Something wrong happened");

    private final HttpStatus httpStatus;
    private final String description;

    DomainErrorMessage(HttpStatus httpStatus, String description) {
        this.httpStatus = httpStatus;
        this.description = description;
    }

}