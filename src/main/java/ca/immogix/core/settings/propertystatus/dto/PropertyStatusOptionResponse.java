package ca.immogix.core.settings.propertystatus.dto;

import ca.immogix.core.settings.propertystatus.PropertyStatusOption;

import java.time.Instant;
import java.util.UUID;

/** Ce que l'API renvoie au client apres creation (ou lecture) d'un statut de propriete. */
public record PropertyStatusOptionResponse(
        UUID id,
        UUID organizationId,
        String name,
        String description,
        Instant createdAt
) {

    public static PropertyStatusOptionResponse from(PropertyStatusOption propertyStatusOption) {
        return new PropertyStatusOptionResponse(
                propertyStatusOption.getId(),
                propertyStatusOption.getOrganizationId(),
                propertyStatusOption.getName(),
                propertyStatusOption.getDescription(),
                propertyStatusOption.getCreatedAt());
    }
}
