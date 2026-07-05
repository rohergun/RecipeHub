package io.github.rohergun.recipe_hub.user;

import io.github.rohergun.recipe_hub.user.dtos.UserProfileResponse;
import io.github.rohergun.recipe_hub.user.dtos.UserProfileUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class AppUserController {
    private final AppUserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUser(
            @PathVariable String username) {
        return ResponseEntity.ok().body(userService.getUserProfile(username));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            UUID id,
            @RequestBody @Valid UserProfileUpdateRequest request){
        return ResponseEntity.ok(userService.updateUserProfile(id, request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
