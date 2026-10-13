package ca.immogix.core.settings.apartmenttype;

import ca.immogix.core.property.UnitRepository;
import ca.immogix.core.settings.SettingsItemInUseException;
import ca.immogix.core.settings.SettingsItemNotFoundException;
import ca.immogix.core.settings.apartmenttype.dto.ApartmentTypeResponse;
import ca.immogix.core.settings.apartmenttype.dto.CreateApartmentTypeRequest;
import ca.immogix.core.settings.apartmenttype.dto.UpdateApartmentTypeRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * CRUD sur ApartmentType. Aucune logique de securite ici : le controleur
 * verifie le tenant_id via TenantClaims avant/apres appel.
 */
@Service
public class ApartmentTypeService {

    // Catalogue de depart insere pour une organisation qui n'a encore
    // aucun type d'appartement (voir listByOrganization) -- reprend les
    // valeurs de l'ancien enum UnitType pour que la migration soit
    // transparente pour les organisations existantes.
    private static final List<String[]> DEFAULT_CATALOG = List.of(
            new String[] {"Studio", "Studio"},
            new String[] {"1 Bedroom", "1 chambre"},
            new String[] {"2 Bedrooms", "2 chambres"},
            new String[] {"3 Bedrooms", "3 chambres"},
            new String[] {"4+ Bedrooms", "4 chambres et plus"});

    private final ApartmentTypeRepository apartmentTypeRepository;
    private final UnitRepository unitRepository;

    public ApartmentTypeService(ApartmentTypeRepository apartmentTypeRepository, UnitRepository unitRepository) {
        this.apartmentTypeRepository = apartmentTypeRepository;
        this.unitRepository = unitRepository;
    }

    public ApartmentTypeResponse create(CreateApartmentTypeRequest request) {
        ApartmentType saved = apartmentTypeRepository.save(
                new ApartmentType(request.organizationId(), request.nameEn(), request.nameFr(), request.description()));
        return ApartmentTypeResponse.from(saved);
    }

    public ApartmentTypeResponse getById(UUID id) {
        return ApartmentTypeResponse.from(getEntityById(id));
    }

    public List<ApartmentTypeResponse> listByOrganization(UUID organizationId) {
        List<ApartmentType> existing = apartmentTypeRepository.findByOrganizationIdOrderByNameEnAsc(organizationId);
        if (existing.isEmpty()) {
            existing = seedDefaults(organizationId);
        }
        return existing.stream().map(ApartmentTypeResponse::from).toList();
    }

    public ApartmentTypeResponse update(UUID id, UpdateApartmentTypeRequest request) {
        ApartmentType apartmentType = getEntityById(id);
        apartmentType.update(request.nameEn(), request.nameFr(), request.description());
        return ApartmentTypeResponse.from(apartmentTypeRepository.save(apartmentType));
    }

    public void delete(UUID id) {
        if (unitRepository.existsByApartmentTypeId(id)) {
            throw new SettingsItemInUseException("type d'appartement", id);
        }
        apartmentTypeRepository.delete(getEntityById(id));
    }

    public ApartmentType getEntityById(UUID id) {
        return apartmentTypeRepository.findById(id)
                .orElseThrow(() -> new SettingsItemNotFoundException("type d'appartement", id));
    }

    // Pre-remplissage a la demande : la premiere fois qu'une organisation
    // consulte Parametres > Types d'appartement (liste vide), on lui
    // insere un catalogue de depart plutot que de la laisser devant un
    // ecran vide -- elle reste ensuite libre de le modifier/supprimer.
    private List<ApartmentType> seedDefaults(UUID organizationId) {
        List<ApartmentType> seeded = DEFAULT_CATALOG.stream()
                .map(pair -> new ApartmentType(organizationId, pair[0], pair[1], null))
                .toList();
        return apartmentTypeRepository.saveAll(seeded);
    }
}
