package fr.troisil.rodez.description_image.domain;

import java.util.Set;
import org.springframework.http.MediaType;

/**
 * Une image encodée en Base64, accompagnée de son type MIME.
 */
public class Image {

    public static final Set<String> SUPPORTED_MIME_TYPES = Set.of(
        MediaType.IMAGE_PNG_VALUE,
        MediaType.IMAGE_JPEG_VALUE,
        MediaType.IMAGE_GIF_VALUE
    );

    private final String base64EncodedImage;
    private final String mimeType;

    /**
     * @throws IllegalArgumentException si le contenu est absent ou vide,
     *         ou si le type MIME n'est pas pris en charge
     */
    public Image(String base64EncodedImage, String mimeType) {
        if (base64EncodedImage == null || base64EncodedImage.isBlank()) {
            throw new IllegalArgumentException(
                "L'image ne peut pas être vide."
            );
        }
        if (mimeType == null || !SUPPORTED_MIME_TYPES.contains(mimeType)) {
            throw new IllegalArgumentException(
                "Type d'image non pris en charge."
            );
        }
        this.base64EncodedImage = base64EncodedImage;
        this.mimeType = mimeType;
    }

    public String getBase64EncodedImage() {
        return this.base64EncodedImage;
    }

    public String getMimeType() {
        return this.mimeType;
    }
}
