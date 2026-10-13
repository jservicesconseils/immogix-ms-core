package ca.immogix.core.property.dto;

import ca.immogix.core.property.Unit;
import ca.immogix.core.property.UnitStatus;
import ca.immogix.core.settings.dto.OptionRef;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Vue PUBLIQUE minimale d'une unite, destinee a un candidat non
 * authentifie qui consulte l'annonce avant de postuler (voir
 * PropertyController.getUnitListing). Ne contient jamais organizationId
 * ni d'autres champs internes -- uniquement ce qu'une annonce affiche.
 */
public record UnitListingResponse(
        UUID propertyId,
        UUID unitId,
        String unitLabel,
        OptionRef apartmentType,
        Integer bedrooms,
        Integer bathrooms,
        Double areaSquareMeters,
        BigDecimal listedRent,
        String propertyStreet,
        String propertyCity,
        String propertyProvince,
        UnitStatus status
) {

    public static UnitListingResponse from(Unit unit, OptionRef apartmentType) {
        return new UnitListingResponse(
                unit.getProperty().getId(),
                unit.getId(),
                unit.getLabel(),
                apartmentType,
                unit.getBedrooms(),
                unit.getBathrooms(),
                unit.getAreaSquareMeters(),
                unit.getListedRent(),
                unit.getProperty().getStreet(),
                unit.getProperty().getCity(),
                unit.getProperty().getProvince(),
                unit.getStatus());
    }
}
