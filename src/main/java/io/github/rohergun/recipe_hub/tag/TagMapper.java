package io.github.rohergun.recipe_hub.tag;

import io.github.rohergun.recipe_hub.tag.dtos.TagResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    @Mapping(source = "createdBy.username", target = "createdBy")
    TagResponse toResponse(Tag tag);
}
