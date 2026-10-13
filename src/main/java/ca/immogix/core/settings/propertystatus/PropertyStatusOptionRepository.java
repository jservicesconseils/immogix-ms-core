package ca.immogix.core.settings.propertystatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PropertyStatusOptionRepository extends JpaRepository<PropertyStatusOption, UUID> {

    List<PropertyStatusOption> findByOrganizationIdOrderByNameAsc(UUID organizationId);
}
