package ca.immogix.core.settings.apartmenttype.dto;

import ca.immogix.core.settings.apartmenttype.ApartmentType;
import ca.immogix.core.settings.dto.OptionRef;

import java.time.Instant;
import java.util.UUID;

/** Ce que l'API renvoie au client apres creation (ou lecture) d'un type d'appartement. */
public record ApartmentTypeResponse(
        UUID id,
        UUID organizationId,
        String nameEn,
        String nameFr,
        String description,
        Instant createdAt
) {

    public static ApartmentTypeResponse from(ApartmentType apartmentType) {
        return new ApartmentTypeResponse(
                apartmentType.getId(),
                apartmentType.getOrganizationId(),
                apartmentType.getNameEn(),
                apartmentType.getNameFr(),
                apartmentType.getDescription(),
                apartmentType.getCreatedAt());
    }

    // Embarque dans UnitResponse.apartmentType : voir OptionRef.
    public OptionRef toRef() {
        return new OptionRef(id, nameEn, nameFr);
    }
}
