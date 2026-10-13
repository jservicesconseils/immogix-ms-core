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
 * renovation", "Vendu"), gere dans Parametres et reference par
 * Property.propertyStatusId. Nomme "Option" pour ne pas entrer en
 * collision avec l'ancien enum Java ca.immogix.core.property.PropertyStatus
 * (retire au profit de cette liste configurable). Un seul nom par
 * entree -- voir ApartmentType. severity pilote la couleur du badge
 * affiche cote client (PrimeNG p-tag : success/warn/danger/secondary/info),
 * puisque l'ancien enum fixe ne peut plus servir de cle pour cette palette.
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

    @Column(nullable = false, length = 20)
    private String severity;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected PropertyStatusOption() {
        // constructeur requis par JPA, ne pas utiliser directement
    }

    public PropertyStatusOption(UUID organizationId, String name, String description, String severity) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        this.severity = severity;
        this.createdAt = Instant.now();
    }

    public void update(String name, String description, String severity) {
        this.name = name;
        this.description = description;
        this.severity = severity;
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

    public String getSeverity() {
        return severity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
