package io.github.rohergun.recipe_hub.user;

import io.github.rohergun.recipe_hub.user.dtos.UserProfileResponse;
import io.github.rohergun.recipe_hub.user.dtos.UserProfileUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


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
            @AuthenticationPrincipal AppUser user,
            @RequestBody @Valid UserProfileUpdateRequest request){
        return ResponseEntity.ok(userService.updateUserProfile(user.getId(), request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(
            @AuthenticationPrincipal AppUser user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

}
