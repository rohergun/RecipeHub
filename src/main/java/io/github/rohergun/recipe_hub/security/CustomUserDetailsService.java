package io.github.rohergun.recipe_hub.security;

import io.github.rohergun.recipe_hub.exception.DomainErrorMessage;
import io.github.rohergun.recipe_hub.exception.RecipeHubException;
import io.github.rohergun.recipe_hub.user.AppUser;
import io.github.rohergun.recipe_hub.user.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final AppUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RecipeHubException(DomainErrorMessage.USER_NOT_FOUND));

        return new CustomUserDetails(user);
    }
}
