package ca.immogix.core.settings.apartmenttype;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Type d'appartement configurable par organisation (ex. "3 1/2", "4 1/2",
 * "Studio"), gere dans Parametres et reference par Unit.apartmentTypeId.
 * Un seul nom par entree : un utilisateur travaille dans une seule
 * langue a la fois (voir I18nService cote client), la saisie n'est
 * jamais bilingue -- le nom est simplement celui que l'organisation a
 * tape, dans la langue qu'elle utilisait a ce moment-la.
 */
@Entity
@Table(name = "apartment_types")
public class ApartmentType {

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

    protected ApartmentType() {
        // constructeur requis par JPA, ne pas utiliser directement
    }

    public ApartmentType(UUID organizationId, String name, String description) {
        this.organizationId = organizationId;
        this.name = name;
        this.description = description;
        this.createdAt = Instant.now();
    }

    // Pas de setters : seule cette methode permet de modifier l'entite
    // apres creation (voir ApartmentTypeService#update), meme convention
    // que Property.update().
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
