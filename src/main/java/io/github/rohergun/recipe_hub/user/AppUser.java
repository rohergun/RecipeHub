package io.github.rohergun.recipe_hub.user;

import io.github.rohergun.recipe_hub.model.NamedEntity;
import io.github.rohergun.recipe_hub.recipe.Recipe;
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

    @Column(length = 500)
    private String bio;

    @Column(length = 50, unique = true, nullable = false)
    @NotBlank
    private String username;

    @Builder.Default
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<Recipe> recipes = new ArrayList<>();
}
