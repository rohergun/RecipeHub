package io.github.rohergun.recipe_hub.user;

import io.github.rohergun.recipe_hub.exception.DomainErrorMessage;
import io.github.rohergun.recipe_hub.exception.RecipeHubException;
import io.github.rohergun.recipe_hub.user.dtos.UserProfileResponse;
import io.github.rohergun.recipe_hub.user.dtos.UserProfileUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService{
    private final AppUserRepository userRepo;
    private final AppUserMapper userMapper;

    @Override
    public UserProfileResponse getUserProfile(String username) {
        AppUser user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RecipeHubException(DomainErrorMessage.USER_NOT_FOUND));
        return userMapper.toResponse(user);
    }

    @Override
    public UserProfileResponse updateUserProfile(UUID id, UserProfileUpdateRequest request) {
        AppUser user = userRepo.findById(id)
                .orElseThrow(() -> new RecipeHubException(DomainErrorMessage.USER_NOT_FOUND));

        if (!user.getUsername().equals(request.username()) &&
            userRepo.existsByUsername(request.username())) {
            throw new RecipeHubException(DomainErrorMessage.USERNAME_ALREADY_EXISTS);
        }

        userMapper.updateUserFromRequest(request, user);
        userRepo.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    public void deleteUser(UUID id) {
        AppUser user = userRepo.findById(id)
                .orElseThrow(() -> new RecipeHubException(DomainErrorMessage.USER_NOT_FOUND));
        userRepo.delete(user);
    }
}
