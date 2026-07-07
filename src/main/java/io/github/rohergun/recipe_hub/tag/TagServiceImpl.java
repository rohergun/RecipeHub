package io.github.rohergun.recipe_hub.tag;


import io.github.rohergun.recipe_hub.exception.DomainErrorMessage;
import io.github.rohergun.recipe_hub.exception.RecipeHubException;
import io.github.rohergun.recipe_hub.tag.dtos.TagRequest;
import io.github.rohergun.recipe_hub.tag.dtos.TagResponse;
import io.github.rohergun.recipe_hub.user.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new RecipeHubException(DomainErrorMessage.TAG_ALREADY_EXISTS);
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
                .orElseThrow(() -> new RecipeHubException(DomainErrorMessage.TAG_NOT_FOUND));

        if (!tag.getCreatedBy().getId().equals(userId)) {
            throw new RecipeHubException(DomainErrorMessage.ACCESS_DENIED);
        }
        tag.setName(request.name());
        tagRepo.save(tag);

        return tagMapper.toResponse(tag);
    }

    @Override
    @Transactional
    public void deleteTag(UUID userId, UUID tagId) {
        Tag tag = tagRepo.findById(tagId)
                .orElseThrow(() -> new RecipeHubException(DomainErrorMessage.TAG_NOT_FOUND));
        if (!tag.getCreatedBy().getId().equals(userId)) {
            throw new RecipeHubException(DomainErrorMessage.ACCESS_DENIED);
        }
        tagRepo.delete(tag);
    }
}
