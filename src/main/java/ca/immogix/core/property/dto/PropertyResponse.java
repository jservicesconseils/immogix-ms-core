package ca.immogix.core.property.dto;

import ca.immogix.core.property.BuildingStatus;
import ca.immogix.core.property.Property;
import ca.immogix.core.settings.dto.OptionRef;
import ca.immogix.core.settings.propertystatus.dto.PropertyStatusOptionResponse.PropertyStatusRef;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Ce que l'API renvoie au client apres creation (ou lecture) d'une
 * propriete. propertyType/propertyStatus sont resolus par PropertyService
 * (appel a PropertyTypeOptionService/PropertyStatusOptionService) et
 * embarques directement ici -- le client n'a pas besoin d'un aller-retour
 * supplementaire pour afficher le nom (nameEn/nameFr) ou la couleur du
 * badge de statut.
 */
public record PropertyResponse(
        UUID id,
        UUID organizationId,
        OptionRef propertyType,
        String name,
        String street,
        String city,
        String province,
        String postalCode,
        PropertyStatusRef propertyStatus,
        String cadastreNumber,
        String taxId,
        BuildingStatus buildingStatus,
        Integer yearBuilt,
        Integer floorCount,
        Double totalSurfaceArea,
        BigDecimal estimatedValue,
        String description,
        Instant createdAt
) {

    public static PropertyResponse from(Property property, OptionRef propertyType, PropertyStatusRef propertyStatus) {
        return new PropertyResponse(
                property.getId(),
                property.getOrganizationId(),
                propertyType,
                property.getName(),
                property.getStreet(),
                property.getCity(),
                property.getProvince(),
                property.getPostalCode(),
                propertyStatus,
                property.getCadastreNumber(),
                property.getTaxId(),
                property.getBuildingStatus(),
                property.getYearBuilt(),
                property.getFloorCount(),
                property.getTotalSurfaceArea(),
                property.getEstimatedValue(),
                property.getDescription(),
                property.getCreatedAt());
    }
}
