package ca.immogix.core.settings.propertystatus.dto;

import ca.immogix.core.settings.propertystatus.PropertyStatusOption;

import java.time.Instant;
import java.util.UUID;

/** Ce que l'API renvoie au client apres creation (ou lecture) d'un statut de propriete. */
public record PropertyStatusOptionResponse(
        UUID id,
        UUID organizationId,
        String nameEn,
        String nameFr,
        String description,
        String severity,
        Instant createdAt
) {

    public static PropertyStatusOptionResponse from(PropertyStatusOption propertyStatusOption) {
        return new PropertyStatusOptionResponse(
                propertyStatusOption.getId(),
                propertyStatusOption.getOrganizationId(),
                propertyStatusOption.getNameEn(),
                propertyStatusOption.getNameFr(),
                propertyStatusOption.getDescription(),
                propertyStatusOption.getSeverity(),
                propertyStatusOption.getCreatedAt());
    }

    // Embarque dans PropertyResponse.propertyStatus : etend OptionRef
    // avec severity pour que le badge colore se construise sans appel
    // supplementaire cote client.
    public PropertyStatusRef toRef() {
        return new PropertyStatusRef(id, nameEn, nameFr, severity);
    }

    public record PropertyStatusRef(UUID id, String nameEn, String nameFr, String severity) {
    }
}
