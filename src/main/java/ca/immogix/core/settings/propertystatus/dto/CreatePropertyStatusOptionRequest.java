package ca.immogix.core.settings.propertystatus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/** Ce que le client envoie pour creer un statut de propriete. */
public record CreatePropertyStatusOptionRequest(

        @NotNull(message = "l'organisation est requise")
        UUID organizationId,

        @NotBlank(message = "le nom anglais est requis")
        @Size(max = 100, message = "le nom anglais ne doit pas depasser 100 caracteres")
        String nameEn,

        @NotBlank(message = "le nom francais est requis")
        @Size(max = 100, message = "le nom francais ne doit pas depasser 100 caracteres")
        String nameFr,

        @Size(max = 300, message = "la description ne doit pas depasser 300 caracteres")
        String description,

        @NotBlank(message = "la couleur est requise")
        @Pattern(regexp = "success|warn|danger|secondary|info", message = "la couleur doit etre success, warn, danger, secondary ou info")
        String severity
) {
}
