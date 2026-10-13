package ca.immogix.core.property.dto;

import ca.immogix.core.property.Unit;
import ca.immogix.core.property.UnitStatus;
import ca.immogix.core.settings.dto.OptionRef;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Ce que l'API renvoie au client apres creation (ou lecture) d'une unite. */
public record UnitResponse(
        UUID id,
        UUID propertyId,
        String label,
        boolean principal,
        Integer floor,
        Double areaSquareMeters,
        Integer bedrooms,
        Integer bathrooms,
        OptionRef apartmentType,
        String description,
        BigDecimal listedRent,
        BigDecimal listedSecurityDeposit,
        UnitStatus status,
        Instant createdAt
) {

    // apartmentType est deja resolu par UnitService (null si
    // unit.getApartmentTypeId() est null -- le type est optionnel).
    public static UnitResponse from(Unit unit, OptionRef apartmentType) {
        return new UnitResponse(
                unit.getId(),
                unit.getProperty().getId(),
                unit.getLabel(),
                unit.isPrincipal(),
                unit.getFloor(),
                unit.getAreaSquareMeters(),
                unit.getBedrooms(),
                unit.getBathrooms(),
                apartmentType,
                unit.getDescription(),
                unit.getListedRent(),
                unit.getListedSecurityDeposit(),
                unit.getStatus(),
                unit.getCreatedAt());
    }
}
