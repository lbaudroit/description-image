package fr.troisil.rodez.description_image.service;

import fr.troisil.rodez.description_image.domain.Description;
import fr.troisil.rodez.description_image.domain.Image;

/** Doublure de développement : renvoie un message fixe, sans analyser l'image. */
public class FauxDescripteur implements DescripteurImage {

    @Override
    public Description decrire(Image image, String question) {
        if (image == null) {
            throw new IllegalArgumentException("Une image est obligatoire.");
        }
        return new Description(
            "Aucune description n'est disponible pour cette image."
        );
    }
}
