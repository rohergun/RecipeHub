package io.github.rohergun.recipe_hub.user;

import io.github.rohergun.recipe_hub.user.dtos.UserProfileResponse;
import io.github.rohergun.recipe_hub.user.dtos.UserProfileUpdateRequest;

import java.util.UUID;

public interface AppUserService {
    UserProfileResponse getUserProfile(String username);
    UserProfileResponse updateUserProfile(UUID id, UserProfileUpdateRequest request);
    void deleteUser(UUID id);
}
