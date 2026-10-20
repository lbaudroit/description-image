package fr.troisil.rodez.description_image.domain;

/**
 * Une image encodée en Base64.
 */
public class Image {

    private final String base64EncodedImage;

    public Image(String base64EncodedImage) {
        this.base64EncodedImage = base64EncodedImage;
    }

    public String getBase64EncodedImage() {
        return base64EncodedImage;
    }
}
