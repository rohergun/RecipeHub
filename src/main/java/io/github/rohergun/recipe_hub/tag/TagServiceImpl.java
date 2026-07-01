package io.github.rohergun.recipe_hub.tag;


import io.github.rohergun.recipe_hub.tag.dtos.TagRequest;
import io.github.rohergun.recipe_hub.tag.dtos.TagResponse;
import io.github.rohergun.recipe_hub.user.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService{
    private final TagRepository tagRepo;
    private final TagMapper tagMapper;
    private final AppUserRepository userRepo;

    @Override
    public Page<TagResponse> listAllTags(Pageable pageable) {
        return tagRepo.findAll(pageable)
                .map(tagMapper::toResponse);
    }

    @Override
    public Page<TagResponse> listTagsByUser(UUID userId, Pageable pageable) {
        return tagRepo.findAllByCreatedById(userId, pageable)
                .map(tagMapper::toResponse);
    }

    @Override
    public Page<TagResponse> listTagsByRecipe(UUID recipeId, Pageable pageable) {
        return tagRepo.findAllByRecipes_Id(recipeId, pageable)
                .map(tagMapper::toResponse);
    }

    @Override
    @Transactional
    public TagResponse addTag(UUID userId, TagRequest request) {
        if (tagRepo.existsByNameAndCreatedById(request.name(), userId)) {
            throw new IllegalArgumentException("Tag is already exits");
        }

        Tag newTag = new Tag();
        newTag.setName(request.name());
        newTag.setCreatedBy(userRepo.getReferenceById(userId));

        tagRepo.save(newTag);
        return tagMapper.toResponse(newTag);
    }

    @Override
    @Transactional
    public TagResponse updateTag(UUID userId, UUID tagId, TagRequest request) {
        Tag tag = tagRepo.findById(tagId)
                .orElseThrow(() -> new NoSuchElementException("Tag not found"));

        if (!tag.getCreatedBy().getId().equals(userId)) {
            throw new AccessDeniedException("You dont have permission to update this tag");
        }
        tag.setName(request.name());
        tagRepo.save(tag);

        return tagMapper.toResponse(tag);
    }

    @Override
    @Transactional
    public void deleteTag(UUID userId, UUID tagId) {
        Tag tag = tagRepo.findById(tagId)
                .orElseThrow(() -> new NoSuchElementException("Tag not found"));
        if (!tag.getCreatedBy().getId().equals(userId)) {
            throw new AccessDeniedException("You dont have permission to delete this tag");
        }
        tagRepo.delete(tag);
    }
}
