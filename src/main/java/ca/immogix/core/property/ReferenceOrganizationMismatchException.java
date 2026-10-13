package ca.immogix.core.property;

import java.util.UUID;

/**
 * Levee quand une propriete ou une unite reference un element de
 * Parametres (type de propriete, statut de propriete, type d'appartement)
 * qui n'appartient pas a la meme organisation (tenant) -- defense en
 * profondeur en plus de la verification de tenant_id de l'appelant, meme
 * principe que OwnerOrganizationMismatchException. Traduite en reponse
 * HTTP 409 (Conflict) par PropertyExceptionHandler.
 */
public class ReferenceOrganizationMismatchException extends RuntimeException {

    public ReferenceOrganizationMismatchException(String referenceLabel, UUID referenceId) {
        super("Le " + referenceLabel + " " + referenceId + " n'appartient pas a la meme organisation");
    }
}
