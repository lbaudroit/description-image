package fr.troisil.rodez.description_image.service;

import fr.troisil.rodez.description_image.domain.Description;
import fr.troisil.rodez.description_image.domain.Image;
import fr.troisil.rodez.description_image.domain.exceptions.DescriptionImpossibleException;

/**
 * Génère une audiodescription à partir d'une image et d'une demande facultative.
 */
public interface DescripteurImage {
    /**
     * Décrit le contenu d'une image en français, en quelques phrases,
     * ou répond à la question posée sur cette image.
     *
     * @param image l'image encodée en Base64, obligatoire et non vide
     * @param question la demande complémentaire, ou {@code null} ou une chaîne
     *                 blanche pour une audiodescription générale
     * @return la description ou la réponse, jamais vide
     * @throws DescriptionImpossibleException si l'agent ne peut pas répondre
     */
    Description decrire(Image image, String question)
        throws DescriptionImpossibleException;
}
