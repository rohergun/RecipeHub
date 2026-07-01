package io.github.rohergun.recipe_hub.recipe;

import io.github.rohergun.recipe_hub.model.NamedEntity;
import io.github.rohergun.recipe_hub.tag.Tag;
import io.github.rohergun.recipe_hub.user.AppUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "recipes")
@NoArgsConstructor
@AllArgsConstructor @Getter @Setter
public class Recipe extends NamedEntity {
    @Column(length = 500)
    @Size(max = 500)
    private String description;

    @ElementCollection
    @NotEmpty
    private List<String> ingredients = new ArrayList<>();

    @Column
    private Integer numberOfLikes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "recipe_tag",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags = new ArrayList<>();
}
