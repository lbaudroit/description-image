package fr.troisil.rodez.description_image.controller;

import fr.troisil.rodez.description_image.domain.Image;
import fr.troisil.rodez.description_image.domain.exceptions.DescriptionImpossibleException;
import fr.troisil.rodez.description_image.service.DescripteurImage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Contrôleur pour le point API de description de l'image
 */
@RestController
@RequestMapping("/api")
public class DescriptionController {

    private final DescripteurImage descripteurImage;

    public static final MediaType[] SUPPORTED_MEDIA_TYPES = {
            MediaType.IMAGE_PNG,
            MediaType.IMAGE_JPEG,
            MediaType.IMAGE_GIF
    };

    public static final String SUPPORTED_MEDIA_TYPES_STRING = Arrays.stream(SUPPORTED_MEDIA_TYPES)
            .map(t -> t.toString().split("/")[1].toUpperCase())
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
        try {
            if (fichier.getContentType() == null) {
                return ResponseEntity.badRequest().body(
                        "Aucun type de fichier défini. Types supportés : " + SUPPORTED_MEDIA_TYPES_STRING
                );
            } else if (isSupportedMediaType(fichier)) {
                return ResponseEntity.ok(
                        descripteurImage.decrire(new Image(fichier), question)
                );
            } else {
                return ResponseEntity.badRequest().body(
                        "Type de fichier non supporté. Types supportés : " + SUPPORTED_MEDIA_TYPES_STRING
                );
            }

        } catch (IOException e) {
            return ResponseEntity.badRequest().body(
                "Impossible d'ouvrir le fichier."
            );
        } catch (DescriptionImpossibleException e) {
            return ResponseEntity.internalServerError().body(
                "Impossible de générer une description."
            );
        }
    }

    /**
     * Vérifie qu'un fichier soit dans un format image supporté
     * @param fichier le fichier transmis
     * @return true si le fichier est parmi {@link SUPPORTED_MEDIA_TYPES}, false sinon
     */
    private boolean isSupportedMediaType(@RequestParam MultipartFile fichier) {
        return Arrays.stream(SUPPORTED_MEDIA_TYPES)
                .anyMatch(t -> t.isCompatibleWith(MimeType.valueOf(Objects.requireNonNull(fichier.getContentType()))));
        }
}
