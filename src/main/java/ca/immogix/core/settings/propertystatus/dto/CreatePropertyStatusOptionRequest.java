package ca.immogix.core.settings.propertystatus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/** Ce que le client envoie pour creer un statut de propriete. */
public record CreatePropertyStatusOptionRequest(

        @NotNull(message = "l'organisation est requise")
        UUID organizationId,

        @NotBlank(message = "le nom est requis")
        @Size(max = 100, message = "le nom ne doit pas depasser 100 caracteres")
        String name,

        @Size(max = 300, message = "la description ne doit pas depasser 300 caracteres")
        String description
) {
}
