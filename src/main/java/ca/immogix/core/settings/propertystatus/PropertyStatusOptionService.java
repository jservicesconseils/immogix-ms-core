package ca.immogix.core.settings.propertystatus;

import ca.immogix.core.property.PropertyRepository;
import ca.immogix.core.settings.SettingsItemInUseException;
import ca.immogix.core.settings.SettingsItemNotFoundException;
import ca.immogix.core.settings.propertystatus.dto.CreatePropertyStatusOptionRequest;
import ca.immogix.core.settings.propertystatus.dto.PropertyStatusOptionResponse;
import ca.immogix.core.settings.propertystatus.dto.UpdatePropertyStatusOptionRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * CRUD sur PropertyStatusOption. Aucune logique de securite ici : le
 * controleur verifie le tenant_id via TenantClaims avant/apres appel.
 */
@Service
public class PropertyStatusOptionService {

    // Catalogue de depart insere pour une organisation qui n'a encore
    // aucun statut de propriete -- reprend les valeurs (et couleurs) de
    // l'ancien enum PropertyStatus pour que la migration soit transparente.
    private static final List<String[]> DEFAULT_CATALOG = List.of(
            new String[] {"Vacant", "Vacante", "warn"},
            new String[] {"Active", "Actif", "success"},
            new String[] {"Under renovation", "En rénovation", "warn"},
            new String[] {"Off market", "Hors marché", "secondary"},
            new String[] {"Archived", "Archivée", "secondary"});

    private final PropertyStatusOptionRepository propertyStatusOptionRepository;
    private final PropertyRepository propertyRepository;

    public PropertyStatusOptionService(PropertyStatusOptionRepository propertyStatusOptionRepository, PropertyRepository propertyRepository) {
        this.propertyStatusOptionRepository = propertyStatusOptionRepository;
        this.propertyRepository = propertyRepository;
    }

    public PropertyStatusOptionResponse create(CreatePropertyStatusOptionRequest request) {
        PropertyStatusOption saved = propertyStatusOptionRepository.save(new PropertyStatusOption(
                request.organizationId(), request.nameEn(), request.nameFr(), request.description(), request.severity()));
        return PropertyStatusOptionResponse.from(saved);
    }

    public PropertyStatusOptionResponse getById(UUID id) {
        return PropertyStatusOptionResponse.from(getEntityById(id));
    }

    public List<PropertyStatusOptionResponse> listByOrganization(UUID organizationId) {
        List<PropertyStatusOption> existing = propertyStatusOptionRepository.findByOrganizationIdOrderByNameEnAsc(organizationId);
        if (existing.isEmpty()) {
            existing = seedDefaults(organizationId);
        }
        return existing.stream().map(PropertyStatusOptionResponse::from).toList();
    }

    public PropertyStatusOptionResponse update(UUID id, UpdatePropertyStatusOptionRequest request) {
        PropertyStatusOption propertyStatusOption = getEntityById(id);
        propertyStatusOption.update(request.nameEn(), request.nameFr(), request.description(), request.severity());
        return PropertyStatusOptionResponse.from(propertyStatusOptionRepository.save(propertyStatusOption));
    }

    public void delete(UUID id) {
        if (propertyRepository.existsByPropertyStatusId(id)) {
            throw new SettingsItemInUseException("statut de propriete", id);
        }
        propertyStatusOptionRepository.delete(getEntityById(id));
    }

    public PropertyStatusOption getEntityById(UUID id) {
        return propertyStatusOptionRepository.findById(id)
                .orElseThrow(() -> new SettingsItemNotFoundException("statut de propriete", id));
    }

    private List<PropertyStatusOption> seedDefaults(UUID organizationId) {
        List<PropertyStatusOption> seeded = DEFAULT_CATALOG.stream()
                .map(triplet -> new PropertyStatusOption(organizationId, triplet[0], triplet[1], null, triplet[2]))
                .toList();
        return propertyStatusOptionRepository.saveAll(seeded);
    }
}
