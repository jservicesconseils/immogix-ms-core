package ca.immogix.core.settings.apartmenttype;

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

    private final ApartmentTypeRepository apartmentTypeRepository;

    public ApartmentTypeService(ApartmentTypeRepository apartmentTypeRepository) {
        this.apartmentTypeRepository = apartmentTypeRepository;
    }

    public ApartmentTypeResponse create(CreateApartmentTypeRequest request) {
        ApartmentType saved = apartmentTypeRepository.save(
                new ApartmentType(request.organizationId(), request.name(), request.description()));
        return ApartmentTypeResponse.from(saved);
    }

    public ApartmentTypeResponse getById(UUID id) {
        return ApartmentTypeResponse.from(getEntityById(id));
    }

    public List<ApartmentTypeResponse> listByOrganization(UUID organizationId) {
        return apartmentTypeRepository.findByOrganizationIdOrderByNameAsc(organizationId).stream()
                .map(ApartmentTypeResponse::from)
                .toList();
    }

    public ApartmentTypeResponse update(UUID id, UpdateApartmentTypeRequest request) {
        ApartmentType apartmentType = getEntityById(id);
        apartmentType.update(request.name(), request.description());
        return ApartmentTypeResponse.from(apartmentTypeRepository.save(apartmentType));
    }

    public void delete(UUID id) {
        apartmentTypeRepository.delete(getEntityById(id));
    }

    public ApartmentType getEntityById(UUID id) {
        return apartmentTypeRepository.findById(id)
                .orElseThrow(() -> new SettingsItemNotFoundException("type d'appartement", id));
    }
}
