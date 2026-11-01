package fr.troisil.rodez.description_image.domain;

/**
 * La description d'une image transmise par l'utilisateur.
 */
public class Description {

    private final String texte;

    public Description(String texte) {
        if (texte == null || texte.isBlank()) {
            throw new IllegalArgumentException(
                "La description ne peut pas être vide."
            );
        }
        this.texte = texte;
    }

    public String getTexte() {
        return this.texte;
    }
}
