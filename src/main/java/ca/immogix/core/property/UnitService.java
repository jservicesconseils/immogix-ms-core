package ca.immogix.core.property;

import ca.immogix.core.property.dto.CreateUnitRequest;
import ca.immogix.core.property.dto.UnitListingResponse;
import ca.immogix.core.property.dto.UnitResponse;
import ca.immogix.core.property.dto.UpdateUnitRequest;
import ca.immogix.core.settings.apartmenttype.ApartmentTypeService;
import ca.immogix.core.settings.apartmenttype.dto.ApartmentTypeResponse;
import ca.immogix.core.settings.dto.OptionRef;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CRUD de base sur Unit, toujours au travers de sa Property parente.
 * Comme PropertyService, aucune logique de securite ici : le controleur
 * s'en charge via TenantClaims.
 *
 * apartmentTypeId est un simple UUID sur l'entite (optionnel, pas de
 * relation JPA) referencant ca.immogix.core.settings.apartmenttype.ApartmentType
 * -- resolu en OptionRef via ApartmentTypeService pour construire les DTO.
 */
@Service
public class UnitService {

    private final UnitRepository repository;
    private final PropertyService propertyService;
    private final ApartmentTypeService apartmentTypeService;

    public UnitService(UnitRepository repository, PropertyService propertyService, ApartmentTypeService apartmentTypeService) {
        this.repository = repository;
        this.propertyService = propertyService;
        this.apartmentTypeService = apartmentTypeService;
    }

    public UnitResponse create(UUID propertyId, CreateUnitRequest request) {
        Property property = propertyService.getEntityById(propertyId);
        OptionRef apartmentType = resolveApartmentType(property.getOrganizationId(), request.apartmentTypeId());

        Unit saved = repository.save(new Unit(
                property,
                request.label(),
                request.principal(),
                request.floor(),
                request.areaSquareMeters(),
                request.bedrooms(),
                request.bathrooms(),
                request.apartmentTypeId(),
                request.description(),
                request.listedRent(),
                request.listedSecurityDeposit(),
                request.status()));
        return UnitResponse.from(saved, apartmentType);
    }

    public UnitResponse update(UUID propertyId, UUID unitId, UpdateUnitRequest request) {
        Unit unit = getEntityByIdAndProperty(unitId, propertyId);
        OptionRef apartmentType = resolveApartmentType(unit.getProperty().getOrganizationId(), request.apartmentTypeId());

        unit.update(
                request.label(),
                request.floor(),
                request.areaSquareMeters(),
                request.bedrooms(),
                request.bathrooms(),
                request.apartmentTypeId(),
                request.description(),
                request.listedRent(),
                request.listedSecurityDeposit());
        return UnitResponse.from(repository.save(unit), apartmentType);
    }

    // Appele par LeaseService a la creation d'un bail (cahier des charges,
    // §4 "prise de possession" -> marquer l'unite "Occupee"). Prend
    // directement l'entite deja chargee par l'appelant pour eviter un
    // aller-retour supplementaire en base.
    public void markOccupied(Unit unit) {
        unit.markOccupied();
        repository.save(unit);
    }

    public UnitResponse getById(UUID propertyId, UUID unitId) {
        Unit unit = getEntityByIdAndProperty(unitId, propertyId);
        return UnitResponse.from(unit, resolveApartmentType(unit.getProperty().getOrganizationId(), unit.getApartmentTypeId()));
    }

    // Public (voir PropertyController.getUnitListing) : un candidat non
    // authentifie consulte l'annonce avant de postuler. DTO volontairement
    // distinct de UnitResponse (jamais organizationId ni champs internes).
    public UnitListingResponse getListing(UUID propertyId, UUID unitId) {
        Unit unit = getEntityByIdAndProperty(unitId, propertyId);
        return UnitListingResponse.from(unit, resolveApartmentType(unit.getProperty().getOrganizationId(), unit.getApartmentTypeId()));
    }

    // Public (comme PropertyService.getEntityById) : le module lease a
    // besoin de l'entite Unit brute pour sa propre relation JPA.
    public Unit getEntityById(UUID propertyId, UUID unitId) {
        return getEntityByIdAndProperty(unitId, propertyId);
    }

    public List<UnitResponse> listByProperty(UUID propertyId) {
        // Valide au passage que la propriete existe (404 sinon), avant de
        // renvoyer une liste vide pour un id totalement inconnu.
        Property property = propertyService.getEntityById(propertyId);
        List<Unit> units = repository.findByPropertyId(propertyId);

        // Un seul aller-retour (pas un par unite) : toutes les unites d'une
        // propriete partagent le catalogue de types de la meme organisation.
        Map<UUID, OptionRef> typesById = apartmentTypeService.listByOrganization(property.getOrganizationId()).stream()
                .collect(Collectors.toMap(ApartmentTypeResponse::id, ApartmentTypeResponse::toRef));

        return units.stream()
                .map(unit -> UnitResponse.from(unit, unit.getApartmentTypeId() == null ? null : typesById.get(unit.getApartmentTypeId())))
                .toList();
    }

    private Unit getEntityByIdAndProperty(UUID unitId, UUID propertyId) {
        Unit unit = repository.findById(unitId)
                .orElseThrow(() -> new UnitNotFoundException(unitId));

        // Une unite qui existe mais appartient a une AUTRE propriete est
        // traitee exactement comme une unite inexistante : on ne revele
        // jamais qu'elle existe ailleurs.
        if (!unit.getProperty().getId().equals(propertyId)) {
            throw new UnitNotFoundException(unitId);
        }

        return unit;
    }

    // apartmentTypeId est optionnel : renvoie null plutot que d'appeler le
    // service si aucun type n'est fourni/defini.
    private OptionRef resolveApartmentType(UUID organizationId, UUID apartmentTypeId) {
        if (apartmentTypeId == null) {
            return null;
        }
        ApartmentTypeResponse option = apartmentTypeService.getById(apartmentTypeId);
        if (!organizationId.equals(option.organizationId())) {
            throw new ReferenceOrganizationMismatchException("type d'appartement", apartmentTypeId);
        }
        return option.toRef();
    }
}
