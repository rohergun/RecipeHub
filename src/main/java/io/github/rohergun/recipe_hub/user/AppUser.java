package io.github.rohergun.recipe_hub.user;

import io.github.rohergun.recipe_hub.model.NamedEntity;
import io.github.rohergun.recipe_hub.recipe.Recipe;
import io.github.rohergun.recipe_hub.tag.Tag;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor @Getter @Setter
public class AppUser extends NamedEntity {
    @Column(nullable = false, unique = true)
    @Email
    @NotBlank
    private String email;

    @Column(nullable = false)
    @NotBlank
    private String password;

    @Column(length = 500)
    private String bio;

    @Column(length = 20, unique = true, nullable = false)
    @NotBlank
    private String username;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<Recipe> recipes = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<Tag> tags = new ArrayList<>();
}
