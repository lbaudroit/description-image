package fr.troisil.rodez.description_image.domain;

import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Encoder;

import org.springframework.web.multipart.MultipartFile;

/**
 * Une image à envoyer à l'outil de description
 */
public class Image {
    private final String base64EncodedImage;

    public Image(MultipartFile fichier) throws IOException {
        Encoder base64Encoder = Base64.getEncoder();
        byte[] bytes = fichier.getBytes();
        this.base64EncodedImage = base64Encoder.encodeToString(bytes);
    }

    public String getBase64EncodedImage() {
        return this.base64EncodedImage;
    }
}
