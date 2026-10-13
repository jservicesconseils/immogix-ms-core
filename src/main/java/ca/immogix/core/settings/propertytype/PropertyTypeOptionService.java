package ca.immogix.core.settings.propertytype;

import ca.immogix.core.property.PropertyRepository;
import ca.immogix.core.settings.SettingsItemInUseException;
import ca.immogix.core.settings.SettingsItemNotFoundException;
import ca.immogix.core.settings.propertytype.dto.CreatePropertyTypeOptionRequest;
import ca.immogix.core.settings.propertytype.dto.PropertyTypeOptionResponse;
import ca.immogix.core.settings.propertytype.dto.UpdatePropertyTypeOptionRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * CRUD sur PropertyTypeOption. Aucune logique de securite ici : le
 * controleur verifie le tenant_id via TenantClaims avant/apres appel.
 */
@Service
public class PropertyTypeOptionService {

    private final PropertyTypeOptionRepository propertyTypeOptionRepository;
    private final PropertyRepository propertyRepository;

    public PropertyTypeOptionService(PropertyTypeOptionRepository propertyTypeOptionRepository, PropertyRepository propertyRepository) {
        this.propertyTypeOptionRepository = propertyTypeOptionRepository;
        this.propertyRepository = propertyRepository;
    }

    public PropertyTypeOptionResponse create(CreatePropertyTypeOptionRequest request) {
        PropertyTypeOption saved = propertyTypeOptionRepository.save(
                new PropertyTypeOption(request.organizationId(), request.name(), request.description()));
        return PropertyTypeOptionResponse.from(saved);
    }

    public PropertyTypeOptionResponse getById(UUID id) {
        return PropertyTypeOptionResponse.from(getEntityById(id));
    }

    public List<PropertyTypeOptionResponse> listByOrganization(UUID organizationId) {
        return propertyTypeOptionRepository.findByOrganizationIdOrderByNameAsc(organizationId).stream()
                .map(PropertyTypeOptionResponse::from)
                .toList();
    }

    public PropertyTypeOptionResponse update(UUID id, UpdatePropertyTypeOptionRequest request) {
        PropertyTypeOption propertyTypeOption = getEntityById(id);
        propertyTypeOption.update(request.name(), request.description());
        return PropertyTypeOptionResponse.from(propertyTypeOptionRepository.save(propertyTypeOption));
    }

    public void delete(UUID id) {
        if (propertyRepository.existsByPropertyTypeId(id)) {
            throw new SettingsItemInUseException("type de propriete", id);
        }
        propertyTypeOptionRepository.delete(getEntityById(id));
    }

    public PropertyTypeOption getEntityById(UUID id) {
        return propertyTypeOptionRepository.findById(id)
                .orElseThrow(() -> new SettingsItemNotFoundException("type de propriete", id));
    }
}
