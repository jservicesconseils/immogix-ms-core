package ca.immogix.core.property.dto;

import ca.immogix.core.property.BuildingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/** Ce que le client envoie pour creer une propriete. */
public record CreatePropertyRequest(

        @NotNull(message = "l'organisation est requise")
        UUID organizationId,

        // Reference ca.immogix.core.settings.propertytype.PropertyTypeOption
        // (liste configurable dans Parametres), pas l'ancien enum PropertyType.
        @NotNull(message = "le type de propriete est requis")
        UUID propertyTypeId,

        @Size(max = 200, message = "le nom ne doit pas depasser 200 caracteres")
        String name,

        @NotBlank(message = "la rue est requise")
        @Size(max = 200, message = "la rue ne doit pas depasser 200 caracteres")
        String street,

        @NotBlank(message = "la ville est requise")
        @Size(max = 100, message = "la ville ne doit pas depasser 100 caracteres")
        String city,

        @NotBlank(message = "la province est requise")
        @Size(min = 2, max = 2, message = "la province doit etre un code a 2 lettres (ex. QC, ON)")
        String province,

        @NotBlank(message = "le code postal est requis")
        @Size(max = 10, message = "le code postal ne doit pas depasser 10 caracteres")
        String postalCode,

        // Reference ca.immogix.core.settings.propertystatus.PropertyStatusOption,
        // pas l'ancien enum PropertyStatus (qui defaultait silencieusement a
        // VACANTE -- desormais explicite, comme propertyTypeId).
        @NotNull(message = "le statut de propriete est requis")
        UUID propertyStatusId,

        // Champs optionnels -- supplementaires par rapport au MVP initial,
        // voir Property pour la justification de leur caractere facultatif.
        @Size(max = 50, message = "le numero de cadastre ne doit pas depasser 50 caracteres")
        String cadastreNumber,

        @Size(max = 50, message = "le numero fiscal ne doit pas depasser 50 caracteres")
        String taxId,

        BuildingStatus buildingStatus,

        Integer yearBuilt,

        Integer floorCount,

        Double totalSurfaceArea,

        BigDecimal estimatedValue,

        @Size(max = 2000, message = "la description ne doit pas depasser 2000 caracteres")
        String description
) {
}
