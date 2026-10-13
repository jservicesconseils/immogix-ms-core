package ca.immogix.core.settings.propertytype.dto;

import ca.immogix.core.settings.propertytype.PropertyTypeOption;

import java.time.Instant;
import java.util.UUID;

/** Ce que l'API renvoie au client apres creation (ou lecture) d'un type de propriete. */
public record PropertyTypeOptionResponse(
        UUID id,
        UUID organizationId,
        String name,
        String description,
        Instant createdAt
) {

    public static PropertyTypeOptionResponse from(PropertyTypeOption propertyTypeOption) {
        return new PropertyTypeOptionResponse(
                propertyTypeOption.getId(),
                propertyTypeOption.getOrganizationId(),
                propertyTypeOption.getName(),
                propertyTypeOption.getDescription(),
                propertyTypeOption.getCreatedAt());
    }
}
