package ca.immogix.core.settings.propertytype.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Ce que le client envoie pour modifier un type de propriete. */
public record UpdatePropertyTypeOptionRequest(

        @NotBlank(message = "le nom est requis")
        @Size(max = 100, message = "le nom ne doit pas depasser 100 caracteres")
        String name,

        @Size(max = 300, message = "la description ne doit pas depasser 300 caracteres")
        String description
) {
}
