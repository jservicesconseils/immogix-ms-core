package ca.immogix.core.settings.dto;

import java.util.UUID;

/**
 * Reference legere vers une entree d'une liste Parametres (type
 * d'appartement, type ou statut de propriete), embarquee dans les
 * reponses d'autres entites (ex. PropertyResponse.propertyType) pour
 * eviter un aller-retour supplementaire cote client : l'id sert aux
 * formulaires d'edition, nameEn/nameFr a l'affichage selon la langue
 * active.
 */
public record OptionRef(UUID id, String nameEn, String nameFr) {
}
