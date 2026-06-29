package io.github.rohergun.recipe_hub.recipe;

import io.github.rohergun.recipe_hub.model.NamedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
    @NotBlank
    private List<String> ingredients = new ArrayList<>();

    @Column
    private Integer numberOfLikes;
}
