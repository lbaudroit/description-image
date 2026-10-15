package fr.troisil.rodez.description_image.service;

import fr.troisil.rodez.description_image.domain.Description;
import fr.troisil.rodez.description_image.domain.Image;

public class FauxDescripteur implements DescripteurImage {

    public Description decrire(Image image, String question) {
        return new Description("Aucune description n'est disponible pour cette image.");
    }
}
