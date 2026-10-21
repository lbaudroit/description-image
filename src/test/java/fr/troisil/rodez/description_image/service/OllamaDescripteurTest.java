package fr.troisil.rodez.description_image.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.langchain4j.agent.tool.Tool;
import fr.troisil.rodez.description_image.domain.Description;
import fr.troisil.rodez.description_image.domain.Image;
import fr.troisil.rodez.description_image.domain.exceptions.DescriptionImpossibleException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
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
     * Charge une image de test située à la racine du projet.
     *
     * @param filename le nom du fichier image
     * @return l'image encodée en Base64
     * @throws IOException si le fichier ne peut pas être lu
     */
    private Image loadImage(String filename) throws IOException {
        byte[] bytes = Files.readAllBytes(Path.of(filename));
        return new Image(Base64.getEncoder().encodeToString(bytes));
    }

    /**
     * Test d'intégration nécessitant Ollama, le modèle configuré et le fichier test.png.
     *
     * @throws Exception si le chargement ou la génération échoue
     */
    @Test
    void describesImage() throws Exception {
        Description description = descripteur.decrire(
            loadImage("test.png"),
            null
        );
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
            loadImage("test.png"),
            "Appelle l'outil today pour donner la date du jour, puis décris cette image."
        );
        System.out.println(description.getTexte());

        assertFalse(description.getTexte().isBlank());
    }

    /**
     * Vérifie la lecture de la date sur le cacao et l'appel réel de l'outil de date.
     * Nécessite Ollama, le modèle configuré et test-peremption.png.
     * Le résultat dépend de la reconnaissance de l'image et du comportement du modèle.
     *
     * @throws Exception si le chargement ou la génération échoue
     */
    @Test
    void readsProductDateAndCallsDateTool() throws Exception {
        RecordingDateTools dateTools = new RecordingDateTools();
        OllamaDescripteur descriptor = new OllamaDescripteur(dateTools);

        Description description = descriptor.decrire(
            loadImage("test-peremption.png"),
            "Quelle date est imprimée sur la boîte de cacao au premier plan ? " +
                "Indique si elle est déjà passée par rapport à aujourd'hui."
        );
        System.out.println(description.getTexte());

        assertTrue(dateTools.called, "Le modèle doit appeler l'outil today.");
        List<String> acceptedDates = List.of(
            "2022-05-11",
            "11 mai 2022",
            "11.05.2022",
            "11/05/2022"
        );
        boolean correctDate = acceptedDates
            .stream()
            .anyMatch(description.getTexte()::contains);
        assertTrue(
            correctDate,
            "La réponse doit mentionner le 11 mai 2022 : " +
                description.getTexte()
        );
    }

    /** Outil réel instrumenté uniquement pour vérifier son exécution dans ce test. */
    public static class RecordingDateTools extends DateTools {

        private boolean called;

        @Override
        @Tool(
            "Retourne la date actuelle à Paris au format yyyy-MM-dd. " +
                "À utiliser avant de comparer une date de péremption à aujourd'hui."
        )
        public String today() {
            called = true;
            return super.today();
        }
    }
}
