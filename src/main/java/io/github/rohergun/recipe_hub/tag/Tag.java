package io.github.rohergun.recipe_hub.tag;

import io.github.rohergun.recipe_hub.model.NamedEntity;
import io.github.rohergun.recipe_hub.recipe.Recipe;
import io.github.rohergun.recipe_hub.user.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags")
@NoArgsConstructor
@AllArgsConstructor @Getter @Setter
public class Tag extends NamedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private List<Recipe> recipes = new ArrayList<>();
}
