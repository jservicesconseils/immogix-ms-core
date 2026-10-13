package ca.immogix.core.settings.apartmenttype;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApartmentTypeRepository extends JpaRepository<ApartmentType, UUID> {

    List<ApartmentType> findByOrganizationIdOrderByNameEnAsc(UUID organizationId);
}
