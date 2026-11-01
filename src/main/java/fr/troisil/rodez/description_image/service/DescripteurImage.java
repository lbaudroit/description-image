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
     * Le contrôleur vérifie la présence et la taille du fichier ainsi que son type
     * annoncé. Image vérifie son contenu non vide et le type MIME accepté à la
     * construction. Chaque implémentation rejette ensuite une image null.
     *
     * @param image l'image encodée en Base64 et son type MIME, jamais null
     * @param question la demande complémentaire, ou {@code null} ou une chaîne
     *                 blanche pour une audiodescription générale
     * @return la description ou la réponse, jamais vide
     * @throws IllegalArgumentException si l'image est absente
     * @throws DescriptionImpossibleException si le fournisseur ne peut pas répondre
     *         ou renvoie une réponse vide ; les fautes d'utilisation ou de
     *         configuration et les bugs internes restent non vérifiés
     */
    Description decrire(Image image, String question)
        throws DescriptionImpossibleException;
}
