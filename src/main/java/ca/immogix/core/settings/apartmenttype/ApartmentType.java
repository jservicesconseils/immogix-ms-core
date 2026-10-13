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
 * Le nom est bilingue (nameEn/nameFr) : l'application supporte l'anglais
 * et le francais, et ces libelles sont affiches directement dans les
 * selecteurs cote client selon la langue active.
 */
@Entity
@Table(name = "apartment_types")
public class ApartmentType {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "name_fr", nullable = false, length = 100)
    private String nameFr;

    @Column(length = 300)
    private String description;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected ApartmentType() {
        // constructeur requis par JPA, ne pas utiliser directement
    }

    public ApartmentType(UUID organizationId, String nameEn, String nameFr, String description) {
        this.organizationId = organizationId;
        this.nameEn = nameEn;
        this.nameFr = nameFr;
        this.description = description;
        this.createdAt = Instant.now();
    }

    // Pas de setters : seule cette methode permet de modifier l'entite
    // apres creation (voir ApartmentTypeService#update), meme convention
    // que Property.update().
    public void update(String nameEn, String nameFr, String description) {
        this.nameEn = nameEn;
        this.nameFr = nameFr;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getNameFr() {
        return nameFr;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
