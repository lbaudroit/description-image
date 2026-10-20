package fr.troisil.rodez.description_image.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import fr.troisil.rodez.description_image.domain.Description;
import fr.troisil.rodez.description_image.domain.Image;
import fr.troisil.rodez.description_image.domain.exceptions.DescriptionImpossibleException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Vérifie les entrées et la génération d'une audiodescription.
 */
public class OllamaDescripteurTest {

    private OllamaDescripteur descripteur;

    @BeforeEach
    void setUp() throws DescriptionImpossibleException {
        descripteur = new OllamaDescripteur();
    }

    @Test
    void rejectsMissingImage() {
        assertThrows(DescriptionImpossibleException.class, () ->
            descripteur.decrire(null, "Décris cette image.")
        );
    }

    @Test
    void rejectsEmptyImage() {
        assertThrows(DescriptionImpossibleException.class, () ->
            descripteur.decrire(new Image(""), null)
        );
    }

    /**
     * Charge uniquement le fichier de test {@code test.png}, situé à la racine du projet.
     *
     * @return l'image encodée en Base64
     * @throws IOException si le fichier ne peut pas être lu
     */
    private Image loadImage() throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of("test.png"));
        return new Image(Base64.getEncoder().encodeToString(bytes));
    }

    /**
     * Test d'intégration nécessitant Ollama, le modèle configuré et le fichier test.png.
     *
     * @throws Exception si le chargement ou la génération échoue
     */
    @Test
    void describesImage() throws Exception {
        Description description = descripteur.decrire(loadImage(), null);
        System.out.println(description.getTexte());

        assertFalse(description.getTexte().isBlank());
    }

    /**
     * Demande explicitement la date pour observer l'appel de l'outil dans la console.
     * Nécessite Ollama, le modèle configuré et test.png.
     * L'appel d'outil est observable dans les logs, mais n'est pas vérifié par cette assertion.
     *
     * @throws Exception si le chargement ou la génération échoue
     */
    @Test
    void describesImageWithCurrentDate() throws Exception {
        Description description = descripteur.decrire(
            loadImage(),
            "Appelle l'outil today pour donner la date du jour, puis décris cette image."
        );
        System.out.println(description.getTexte());

        assertFalse(description.getTexte().isBlank());
    }
}
