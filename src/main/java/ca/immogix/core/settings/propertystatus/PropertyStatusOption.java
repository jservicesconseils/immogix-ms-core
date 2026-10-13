package ca.immogix.core.settings.propertystatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Statut de propriete configurable par organisation (ex. "Actif", "En
 * renovation", "Vendu"), gere dans Parametres. Nomme "Option" pour ne pas
 * entrer en collision avec l'enum Java ca.immogix.core.property.PropertyStatus
 * utilise sur Property.status -- les deux coexistent independamment,
 * cette table alimente uniquement les ecrans de Parametres.
 */
@Entity
@Table(name = "property_status_options")
public class PropertyStatusOption {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected PropertyStatusOption() {
        // constructeur requis par JPA, ne pas utiliser directement
    }

    public PropertyStatusOption(UUID organizationId, String name, String description) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        this.createdAt = Instant.now();
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
