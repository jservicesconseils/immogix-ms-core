package ca.immogix.core.settings.dto;

import java.util.UUID;

/**
 * Reference legere vers une entree d'une liste Parametres (type
 * d'appartement, type ou statut de propriete), embarquee dans les
 * reponses d'autres entites (ex. PropertyResponse.propertyType) pour
 * eviter un aller-retour supplementaire cote client. Un seul nom : voir
 * ApartmentType pour l'explication (pas de saisie bilingue).
 */
public record OptionRef(UUID id, String name) {
}
