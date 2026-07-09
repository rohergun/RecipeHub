package io.github.rohergun.recipe_hub.auth;

import io.github.rohergun.recipe_hub.auth.dtos.AuthResponse;
import io.github.rohergun.recipe_hub.auth.dtos.LoginRequest;
import io.github.rohergun.recipe_hub.auth.dtos.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
