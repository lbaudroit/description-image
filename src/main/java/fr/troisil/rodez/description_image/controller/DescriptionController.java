package fr.troisil.rodez.description_image.controller;

import fr.troisil.rodez.description_image.domain.Image;
import fr.troisil.rodez.description_image.domain.exceptions.DescriptionImpossibleException;
import fr.troisil.rodez.description_image.service.DescripteurImage;
import java.io.IOException;
import java.util.Base64;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Contrôleur pour le point API de description de l'image
 */
@RestController
@RequestMapping("/api")
public class DescriptionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        DescriptionController.class
    );

    private final DescripteurImage descripteurImage;

    public static final String SUPPORTED_MEDIA_TYPES_STRING =
        Image.SUPPORTED_MIME_TYPES.stream()
            .map(t -> t.split("/")[1].toUpperCase())
            .collect(Collectors.joining(", "));

    public DescriptionController(DescripteurImage descripteurImage) {
        this.descripteurImage = descripteurImage;
    }

    /**
     * Génère une description de l'image transmise, en prenant en compte une question de manière optionnelle
     * @param question la question (optionnelle) en lien avec l'image
     * @param fichier l'image envoyée, au format dans {@link SUPPORTED_MEDIA_TYPES}
     * @return une description de l'image envoyée
     */
    @PostMapping("/describe")
    public ResponseEntity<Object> describe(
        @RequestParam(required = false) String question,
        @RequestParam MultipartFile fichier
    ) {
        if (fichier.isEmpty()) {
            return ResponseEntity.badRequest().body("Le fichier est vide.");
        }
        if (fichier.getContentType() == null) {
            return ResponseEntity.badRequest().body(
                "Aucun type de fichier défini. Types supportés : " +
                    SUPPORTED_MEDIA_TYPES_STRING
            );
        }

        MediaType mediaType = extractMediaType(fichier);
        if (mediaType == null) {
            return ResponseEntity.badRequest().body(
                "Type de fichier malformé."
            );
        }
        if (
            Image.SUPPORTED_MIME_TYPES.stream().noneMatch(supportedType ->
                supportedType.equals(mediaType.toString())
            )
        ) {
            return ResponseEntity.badRequest().body(
                "Type de fichier non supporté. Types supportés : " +
                    SUPPORTED_MEDIA_TYPES_STRING
            );
        }

        try {
            byte[] bytes = fichier.getBytes();
            if (bytes.length == 0) {
                return ResponseEntity.badRequest().body("Le fichier est vide.");
            }
            Image image = new Image(
                Base64.getEncoder().encodeToString(bytes),
                mediaType.toString()
            );
            return ResponseEntity.ok(descripteurImage.decrire(image, question));
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.error("Échec de lecture du fichier envoyé.", e);
            return ResponseEntity.internalServerError().body(
                "Impossible de lire le fichier."
            );
        } catch (DescriptionImpossibleException e) {
            LOGGER.error(
                "Échec de génération de la description par le fournisseur.",
                e
            );
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                "Impossible de générer une description."
            );
        }
    }

    /**
     * Extrait le type de média à partir du fichier téléchargé.
     *
     * @param fichier le fichier téléchargé
     * @return le type de média extrait, ou null si le type de média est malformé
     */
    private MediaType extractMediaType(MultipartFile fichier) {
        try {
            MediaType parsedType = MediaType.parseMediaType(
                fichier.getContentType()
            );
            return new MediaType(parsedType.getType(), parsedType.getSubtype());
        } catch (InvalidMediaTypeException e) {
            return null;
        }
    }
}
