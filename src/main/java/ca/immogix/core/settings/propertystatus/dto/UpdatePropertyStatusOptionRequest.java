package ca.immogix.core.settings.propertystatus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Ce que le client envoie pour modifier un statut de propriete. */
public record UpdatePropertyStatusOptionRequest(

        @NotBlank(message = "le nom est requis")
        @Size(max = 100, message = "le nom ne doit pas depasser 100 caracteres")
        String name,

        @Size(max = 300, message = "la description ne doit pas depasser 300 caracteres")
        String description,

        @NotBlank(message = "la couleur est requise")
        @Pattern(regexp = "success|warn|danger|secondary|info", message = "la couleur doit etre success, warn, danger, secondary ou info")
        String severity
) {
}
