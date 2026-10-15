package fr.troisil.rodez.description_image.domain;

/**
 * La description d'une image transmise par l'utilisateur.
 */
public class Description {
    private String texte;

    public Description(String texte) {
        this.texte = texte;
    }

    public String getText() {
        return this.texte;
    }
}
