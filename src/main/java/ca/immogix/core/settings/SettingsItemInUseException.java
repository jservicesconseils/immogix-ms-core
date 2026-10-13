package ca.immogix.core.settings;

import java.util.UUID;

/**
 * Levee quand on tente de supprimer un element de parametre (type
 * d'appartement, type ou statut de propriete) encore reference par au
 * moins une propriete ou une unite -- empeche de laisser une reference
 * orpheline qui ferait echouer la resolution du nom affiche cote client.
 * Traduite en reponse HTTP 409 (Conflict) par SettingsExceptionHandler.
 */
public class SettingsItemInUseException extends RuntimeException {

    public SettingsItemInUseException(String entityLabel, UUID id) {
        super("Le " + entityLabel + " " + id + " est utilise par au moins une propriete ou unite et ne peut pas etre supprime");
    }
}
