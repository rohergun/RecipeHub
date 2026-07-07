package io.github.rohergun.recipe_hub.user;

import io.github.rohergun.recipe_hub.exception.RecipeHubException;
import io.github.rohergun.recipe_hub.user.dtos.UserProfileResponse;
import io.github.rohergun.recipe_hub.user.dtos.UserProfileUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    @Mock
    private AppUserRepository userRepo;

    @Mock
    private AppUserMapper userMapper;

    @InjectMocks
    private AppUserServiceImpl userService;

    private AppUser user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new AppUser();
        ReflectionTestUtils.setField(user, "id", userId);
        user.setUsername("rohergun");
        user.setEmail("rohergun@gmail.com");
    }

    // ---------- getUserProfile ----------

    @Test
    void getUserProfile_returnsProfile_whenUserExists() {
        UserProfileResponse expectedResponse = new UserProfileResponse(
                userId, "rohergun", "roh", null, LocalDateTime.now()
        );

        when(userRepo.findByUsername("rohergun")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserProfileResponse result = userService.getUserProfile("rohergun");

        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepo).findByUsername("rohergun");
    }

    @Test
    void getUserProfile_throws_whenUserNotFound() {
        when(userRepo.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile("ghost"))
                .isInstanceOf(RecipeHubException.class)
                .hasMessageContaining("User not found");
    }

    // ---------- updateUserProfile ----------

    @Test
    void updateUserProfile_updatesAndSaves_whenUsernameUnchanged() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest("rohergun", "new bio");
        UserProfileResponse expectedResponse = new UserProfileResponse(
                userId, "rohergun", "roh", "new bio", LocalDateTime.now()
        );

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserProfileResponse result = userService.updateUserProfile(userId, request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(userMapper).updateUserFromRequest(request, user);
        verify(userRepo).save(user);
        verify(userRepo, never()).existsByUsername(any());
    }

    @Test
    void updateUserProfile_throws_whenNewUsernameAlreadyTaken() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest("taken-name", "new bio");

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.existsByUsername("taken-name")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUserProfile(userId, request))
                .isInstanceOf(RecipeHubException.class)
                .hasMessageContaining("Username is already exists");

        verify(userRepo, never()).save(any());
    }

    @Test
    void updateUserProfile_allowsSave_whenNewUsernameIsAvailable() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest("free-name", "new bio");
        UserProfileResponse expectedResponse = new UserProfileResponse(
                userId, "free-name", "roh", "new bio", LocalDateTime.now()
        );

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userRepo.existsByUsername("free-name")).thenReturn(false);
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserProfileResponse result = userService.updateUserProfile(userId, request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(userRepo).save(user);
    }

    @Test
    void updateUserProfile_throws_whenUserNotFound() {
        UserProfileUpdateRequest request = new UserProfileUpdateRequest("any", "bio");
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUserProfile(userId, request))
                .isInstanceOf(RecipeHubException.class)
                .hasMessageContaining("User not found");

        verify(userRepo, never()).save(any());
    }

    // ---------- deleteUser ----------

    @Test
    void deleteUser_deletes_whenUserExists() {
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepo).delete(user);
    }

    @Test
    void deleteUser_throws_whenUserNotFound() {
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(RecipeHubException.class)
                .hasMessageContaining("User not found");

        verify(userRepo, never()).delete(any());
    }
}