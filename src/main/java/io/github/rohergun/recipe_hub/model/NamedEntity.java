package io.github.rohergun.recipe_hub.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@MappedSuperclass
public class NamedEntity extends BaseEntity{
    @Column
    @NotBlank
    private String name;

    @Override
    public String toString() {
        String name = this.getName();
        return name != null ? name : "<null>";
    }
}
