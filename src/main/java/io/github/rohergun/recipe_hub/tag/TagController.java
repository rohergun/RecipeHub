package io.github.rohergun.recipe_hub.tag;


import io.github.rohergun.recipe_hub.tag.dtos.TagRequest;
import io.github.rohergun.recipe_hub.tag.dtos.TagResponse;
import io.github.rohergun.recipe_hub.user.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;

    @GetMapping
    public ResponseEntity<Page<TagResponse>> getAllTags(
            @PageableDefault(page = 0, size = 10)Pageable pageable){
        return ResponseEntity.ok(tagService.listAllTags(pageable));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<TagResponse>> getTagsByUser(
            @AuthenticationPrincipal AppUser user,
            @PageableDefault(page = 0, size = 10)Pageable pageable){
        return ResponseEntity.ok(tagService.listTagsByUser(user.getId(), pageable));
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<Page<TagResponse>> getTagsByRecipe(
            @PathVariable UUID recipeId,
            @PageableDefault(page = 0, size = 10)Pageable pageable) {
        return ResponseEntity.ok(tagService.listTagsByRecipe(recipeId, pageable));
    }

    @PostMapping("/me")
    public ResponseEntity<TagResponse> createTag(
            @AuthenticationPrincipal AppUser user,
            @RequestBody @Valid TagRequest request){

        TagResponse created = tagService.addTag(user.getId(), request);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/me/{tagId}")
    public ResponseEntity<TagResponse> updateTag(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID tagId,
            @RequestBody @Valid TagRequest request) {
        return ResponseEntity.ok(tagService.updateTag(userId, tagId, request));
    }

    @DeleteMapping("/me/{tagId}")
    public ResponseEntity<Void> deleteTag(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID tagId) {

        tagService.deleteTag(userId, tagId);
        return ResponseEntity.noContent().build();
    }
}
