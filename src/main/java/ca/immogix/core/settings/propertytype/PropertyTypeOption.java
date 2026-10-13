package ca.immogix.core.settings.propertytype;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Type de propriete configurable par organisation (ex. "Residentiel",
 * "Commercial"), gere dans Parametres et reference par Property.propertyTypeId.
 * Nomme "Option" pour ne pas entrer en collision avec l'ancien enum Java
 * ca.immogix.core.property.PropertyType (retire au profit de cette liste
 * configurable). Un seul nom par entree -- voir ApartmentType.
 */
@Entity
@Table(name = "property_type_options")
public class PropertyTypeOption {

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

    protected PropertyTypeOption() {
        // constructeur requis par JPA, ne pas utiliser directement
    }

    public PropertyTypeOption(UUID organizationId, String name, String description) {
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
