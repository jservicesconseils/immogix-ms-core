package ca.immogix.core.property;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {

    // Utilise par le dashboard (§20 "Proprietes et unites").
    long countByOrganizationId(UUID organizationId);

    List<Property> findByOrganizationId(UUID organizationId);

    // Utilises par les services de ca.immogix.core.settings pour empecher
    // la suppression d'un type/statut encore reference par une propriete
    // (voir SettingsItemInUseException).
    boolean existsByPropertyTypeId(UUID propertyTypeId);

    boolean existsByPropertyStatusId(UUID propertyStatusId);
}
