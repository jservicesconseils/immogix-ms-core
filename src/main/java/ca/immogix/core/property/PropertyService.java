package ca.immogix.core.property;

import ca.immogix.core.property.dto.CreatePropertyRequest;
import ca.immogix.core.property.dto.PropertyResponse;
import ca.immogix.core.property.dto.UpdatePropertyRequest;
import ca.immogix.core.settings.dto.OptionRef;
import ca.immogix.core.settings.propertystatus.PropertyStatusOptionService;
import ca.immogix.core.settings.propertystatus.dto.PropertyStatusOptionResponse;
import ca.immogix.core.settings.propertystatus.dto.PropertyStatusOptionResponse.PropertyStatusRef;
import ca.immogix.core.settings.propertytype.PropertyTypeOptionService;
import ca.immogix.core.settings.propertytype.dto.PropertyTypeOptionResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CRUD de base sur Property. Ne contient aucune logique de securite :
 * le controleur verifie le tenant_id via TenantClaims avant/apres appel,
 * ce service reste testable sans dependre du contexte de securite.
 *
 * propertyTypeId/propertyStatusId sont de simples UUID sur l'entite (pas
 * de relation JPA, meme logique que organizationId) : ce service les
 * resout en OptionRef/PropertyStatusRef (nameEn/nameFr, + severity pour
 * le statut) via PropertyTypeOptionService/PropertyStatusOptionService
 * pour construire PropertyResponse.
 */
@Service
public class PropertyService {

    private final PropertyRepository repository;
    private final PropertyTypeOptionService propertyTypeOptionService;
    private final PropertyStatusOptionService propertyStatusOptionService;

    public PropertyService(PropertyRepository repository, PropertyTypeOptionService propertyTypeOptionService,
                            PropertyStatusOptionService propertyStatusOptionService) {
        this.repository = repository;
        this.propertyTypeOptionService = propertyTypeOptionService;
        this.propertyStatusOptionService = propertyStatusOptionService;
    }

    public PropertyResponse create(CreatePropertyRequest request) {
        OptionRef propertyType = resolvePropertyType(request.organizationId(), request.propertyTypeId());
        PropertyStatusRef propertyStatus = resolvePropertyStatus(request.organizationId(), request.propertyStatusId());

        Property saved = repository.save(new Property(
                request.organizationId(),
                request.propertyTypeId(),
                request.name(),
                request.street(),
                request.city(),
                request.province(),
                request.postalCode(),
                request.propertyStatusId(),
                request.cadastreNumber(),
                request.taxId(),
                request.buildingStatus(),
                request.yearBuilt(),
                request.floorCount(),
                request.totalSurfaceArea(),
                request.estimatedValue(),
                request.description()));
        return PropertyResponse.from(saved, propertyType, propertyStatus);
    }

    public PropertyResponse getById(UUID id) {
        Property property = getEntityById(id);
        return PropertyResponse.from(property,
                resolvePropertyType(property.getOrganizationId(), property.getPropertyTypeId()),
                resolvePropertyStatus(property.getOrganizationId(), property.getPropertyStatusId()));
    }

    public PropertyResponse update(UUID id, UpdatePropertyRequest request) {
        Property property = getEntityById(id);
        OptionRef propertyType = resolvePropertyType(property.getOrganizationId(), request.propertyTypeId());
        PropertyStatusRef propertyStatus = resolvePropertyStatus(property.getOrganizationId(), request.propertyStatusId());

        property.update(
                request.propertyTypeId(),
                request.name(),
                request.street(),
                request.city(),
                request.province(),
                request.postalCode(),
                request.propertyStatusId(),
                request.cadastreNumber(),
                request.taxId(),
                request.buildingStatus(),
                request.yearBuilt(),
                request.floorCount(),
                request.totalSurfaceArea(),
                request.estimatedValue(),
                request.description());
        return PropertyResponse.from(repository.save(property), propertyType, propertyStatus);
    }

    public List<PropertyResponse> listByOrganization(UUID organizationId) {
        List<Property> properties = repository.findByOrganizationId(organizationId);

        // Un seul aller-retour par liste (pas un par propriete) : toutes
        // les proprietes d'une organisation partagent le meme catalogue de
        // types/statuts.
        Map<UUID, OptionRef> typesById = propertyTypeOptionService.listByOrganization(organizationId).stream()
                .collect(Collectors.toMap(PropertyTypeOptionResponse::id, PropertyTypeOptionResponse::toRef));
        Map<UUID, PropertyStatusRef> statusesById = propertyStatusOptionService.listByOrganization(organizationId).stream()
                .collect(Collectors.toMap(PropertyStatusOptionResponse::id, PropertyStatusOptionResponse::toRef));

        return properties.stream()
                .map(property -> PropertyResponse.from(property,
                        typesById.get(property.getPropertyTypeId()),
                        statusesById.get(property.getPropertyStatusId())))
                .toList();
    }

    // Public (et pas seulement package-private) volontairement : d'autres
    // modules du monolithe (owner, puis lease/maintenance) ont besoin de
    // l'entite Property brute pour leurs propres relations JPA. Les
    // controleurs, eux, ne manipulent que des DTO (PropertyResponse).
    public Property getEntityById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException(id));
    }

    private OptionRef resolvePropertyType(UUID organizationId, UUID propertyTypeId) {
        PropertyTypeOptionResponse option = propertyTypeOptionService.getById(propertyTypeId);
        assertSameOrganization(organizationId, option.organizationId(), "type de propriete", propertyTypeId);
        return option.toRef();
    }

    private PropertyStatusRef resolvePropertyStatus(UUID organizationId, UUID propertyStatusId) {
        PropertyStatusOptionResponse option = propertyStatusOptionService.getById(propertyStatusId);
        assertSameOrganization(organizationId, option.organizationId(), "statut de propriete", propertyStatusId);
        return option.toRef();
    }

    private void assertSameOrganization(UUID expected, UUID actual, String referenceLabel, UUID referenceId) {
        if (!expected.equals(actual)) {
            throw new ReferenceOrganizationMismatchException(referenceLabel, referenceId);
        }
    }
}
