package ca.immogix.core.settings;

import java.util.UUID;

/**
 * Levee quand aucun element de parametre (type d'appartement, type ou
 * statut de propriete) ne correspond a l'id demande. Commune aux 3
 * sous-modules de settings puisqu'ils partagent la meme forme (nom +
 * description geres par organisation).
 */
public class SettingsItemNotFoundException extends RuntimeException {

    public SettingsItemNotFoundException(String entityLabel, UUID id) {
        super("Aucun " + entityLabel + " trouve avec l'id " + id);
    }
}
