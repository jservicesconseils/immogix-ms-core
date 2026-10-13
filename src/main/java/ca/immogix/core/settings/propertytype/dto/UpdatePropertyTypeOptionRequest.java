package ca.immogix.core.settings.propertytype.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Ce que le client envoie pour modifier un type de propriete. */
public record UpdatePropertyTypeOptionRequest(

        @NotBlank(message = "le nom anglais est requis")
        @Size(max = 100, message = "le nom anglais ne doit pas depasser 100 caracteres")
        String nameEn,

        @NotBlank(message = "le nom francais est requis")
        @Size(max = 100, message = "le nom francais ne doit pas depasser 100 caracteres")
        String nameFr,

        @Size(max = 300, message = "la description ne doit pas depasser 300 caracteres")
        String description
) {
}
