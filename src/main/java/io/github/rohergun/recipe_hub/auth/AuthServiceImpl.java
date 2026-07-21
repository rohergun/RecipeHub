package io.github.rohergun.recipe_hub.auth;

import io.github.rohergun.recipe_hub.auth.dtos.AuthResponse;
import io.github.rohergun.recipe_hub.auth.dtos.LoginRequest;
import io.github.rohergun.recipe_hub.auth.dtos.RegisterRequest;
import io.github.rohergun.recipe_hub.exception.DomainErrorMessage;
import io.github.rohergun.recipe_hub.exception.RecipeHubException;
import io.github.rohergun.recipe_hub.security.CustomUserDetails;
import io.github.rohergun.recipe_hub.security.CustomUserDetailsService;
import io.github.rohergun.recipe_hub.security.JwtService;
import io.github.rohergun.recipe_hub.user.AppUser;
import io.github.rohergun.recipe_hub.user.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RecipeHubException(DomainErrorMessage.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new RecipeHubException(DomainErrorMessage.USERNAME_ALREADY_EXISTS);
        }
        AppUser newUser = new AppUser();
        newUser.setEmail(request.email());
        newUser.setName(request.name());
        newUser.setUsername(request.username());
        newUser.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(newUser);
        return new AuthResponse(jwtService.generateToken(new CustomUserDetails(newUser)));
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RecipeHubException(DomainErrorMessage.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RecipeHubException(DomainErrorMessage.INVALID_CREDENTIALS);
        }
        return new AuthResponse(jwtService.generateToken(new CustomUserDetails(user)));
    }
}
