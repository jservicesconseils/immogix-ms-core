package ca.immogix.core.settings.propertytype;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PropertyTypeOptionRepository extends JpaRepository<PropertyTypeOption, UUID> {

    List<PropertyTypeOption> findByOrganizationIdOrderByNameEnAsc(UUID organizationId);
}
