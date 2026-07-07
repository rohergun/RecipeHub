package io.github.rohergun.recipe_hub.tag;

import io.github.rohergun.recipe_hub.exception.RecipeHubException;
import io.github.rohergun.recipe_hub.tag.dtos.TagRequest;
import io.github.rohergun.recipe_hub.tag.dtos.TagResponse;
import io.github.rohergun.recipe_hub.user.AppUser;
import io.github.rohergun.recipe_hub.user.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepo;

    @Mock
    private TagMapper tagMapper;

    @Mock
    private AppUserRepository userRepo;

    @InjectMocks
    private TagServiceImpl tagService;

    private AppUser owner;
    private AppUser otherUser;
    private Tag tag;
    private UUID ownerId;
    private UUID otherUserId;
    private UUID tagId;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        tagId = UUID.randomUUID();

        owner = new AppUser();
        ReflectionTestUtils.setField(owner, "id", ownerId);
        owner.setUsername("test");

        otherUser = new AppUser();
        ReflectionTestUtils.setField(otherUser, "id", otherUserId);

        tag = new Tag();
        ReflectionTestUtils.setField(tag, "id", tagId);
        tag.setName("vegan");
        tag.setCreatedBy(owner);
    }

    private TagResponse sampleResponse() {
        return new TagResponse(tagId, "vegan", "rohergun", LocalDateTime.now(), LocalDateTime.now());
    }

    // ---------- listAllTags ----------

    @Test
    void listAllTags_returnsPagedResponses() {
        Pageable pageable = Pageable.ofSize(20);
        Page<Tag> page = new PageImpl<>(List.of(tag));

        when(tagRepo.findAll(pageable)).thenReturn(page);
        when(tagMapper.toResponse(tag)).thenReturn(sampleResponse());

        Page<TagResponse> result = tagService.listAllTags(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(tagRepo).findAll(pageable);
    }

    // ---------- listTagsByUser ----------

    @Test
    void listTagsByUser_delegatesToRepository() {
        Pageable pageable = Pageable.ofSize(20);
        Page<Tag> page = new PageImpl<>(List.of(tag));

        when(tagRepo.findAllByCreatedById(ownerId, pageable)).thenReturn(page);
        when(tagMapper.toResponse(tag)).thenReturn(sampleResponse());

        Page<TagResponse> result = tagService.listTagsByUser(ownerId, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(tagRepo).findAllByCreatedById(ownerId, pageable);
    }

    // ---------- listTagsByRecipe ----------

    @Test
    void listTagsByRecipe_delegatesToRepository() {
        UUID recipeId = UUID.randomUUID();
        Pageable pageable = Pageable.ofSize(20);
        Page<Tag> page = new PageImpl<>(List.of(tag));

        when(tagRepo.findAllByRecipes_Id(recipeId, pageable)).thenReturn(page);
        when(tagMapper.toResponse(tag)).thenReturn(sampleResponse());

        Page<TagResponse> result = tagService.listTagsByRecipe(recipeId, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(tagRepo).findAllByRecipes_Id(recipeId, pageable);
    }

    // ---------- addTag ----------

    @Test
    void addTag_createsTag_whenTagDoesNotExist() {
        TagRequest request = new TagRequest("vegan");
        TagResponse expected = sampleResponse();

        when(tagRepo.existsByNameAndCreatedById("vegan", ownerId)).thenReturn(false);
        when(userRepo.getReferenceById(ownerId)).thenReturn(owner);
        when(tagMapper.toResponse(any(Tag.class))).thenReturn(expected);

        TagResponse result = tagService.addTag(ownerId, request);

        assertThat(result).isEqualTo(expected);

        ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
        verify(tagRepo).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("vegan");
        assertThat(captor.getValue().getCreatedBy()).isEqualTo(owner);
    }

    @Test
    void addTag_throws_whenTagAlreadyExists() {
        TagRequest request = new TagRequest("vegan");
        when(tagRepo.existsByNameAndCreatedById("vegan", ownerId)).thenReturn(true);

        assertThatThrownBy(() -> tagService.addTag(ownerId, request))
                .isInstanceOf(RecipeHubException.class)
                .hasMessageContaining("Tag with this name already exists");

        verify(tagRepo, never()).save(any());
    }

    // ---------- updateTag ----------

    @Test
    void updateTag_updatesName_whenUserIsOwner() {
        TagRequest request = new TagRequest("plant-based");
        TagResponse expected = new TagResponse(tagId, "plant-based", "rohergun",
                LocalDateTime.now(), LocalDateTime.now());

        when(tagRepo.findById(tagId)).thenReturn(Optional.of(tag));
        when(tagMapper.toResponse(tag)).thenReturn(expected);

        TagResponse result = tagService.updateTag(ownerId, tagId, request);

        assertThat(result).isEqualTo(expected);
        assertThat(tag.getName()).isEqualTo("plant-based");
        verify(tagRepo).save(tag);
    }

    @Test
    void updateTag_throwsAccessDenied_whenUserIsNotOwner() {
        TagRequest request = new TagRequest("plant-based");
        when(tagRepo.findById(tagId)).thenReturn(Optional.of(tag));

        assertThatThrownBy(() -> tagService.updateTag(otherUserId, tagId, request))
                .isInstanceOf(RecipeHubException.class)
                .hasMessageContaining("You dont have permission to access this resource");

        verify(tagRepo, never()).save(any());
    }

    @Test
    void updateTag_throws_whenTagNotFound() {
        TagRequest request = new TagRequest("plant-based");
        when(tagRepo.findById(tagId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.updateTag(ownerId, tagId, request))
                .isInstanceOf(RecipeHubException.class)
                .hasMessageContaining("Tag not found");
    }

    // ---------- deleteTag ----------

    @Test
    void deleteTag_deletes_whenUserIsOwner() {
        when(tagRepo.findById(tagId)).thenReturn(Optional.of(tag));

        tagService.deleteTag(ownerId, tagId);

        verify(tagRepo).delete(tag);
    }

    @Test
    void deleteTag_throwsAccessDenied_whenUserIsNotOwner() {
        when(tagRepo.findById(tagId)).thenReturn(Optional.of(tag));

        assertThatThrownBy(() -> tagService.deleteTag(otherUserId, tagId))
                .isInstanceOf(RecipeHubException.class)
                .hasMessageContaining("You dont have permission to access this resource");

        verify(tagRepo, never()).delete(any());
    }

    @Test
    void deleteTag_throws_whenTagNotFound() {
        when(tagRepo.findById(tagId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.deleteTag(ownerId, tagId))
                .isInstanceOf(RecipeHubException.class)
                .hasMessageContaining("Tag not found");

        verify(tagRepo, never()).delete(any());
    }
}