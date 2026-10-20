package fr.troisil.rodez.description_image.service;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.tool.ToolArgumentsErrorHandler;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;
import fr.troisil.rodez.description_image.domain.Description;
import fr.troisil.rodez.description_image.domain.Image;
import fr.troisil.rodez.description_image.domain.exceptions.DescriptionImpossibleException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Produit une audiodescription avec un modèle local Ollama et des outils Java.
 * Le prompt principal est chargé depuis {@code /audiodescription-prompt.txt}.
 * La demande de l'utilisateur est envoyée séparément, avec l'image PNG.
 */
public class OllamaDescripteur implements DescripteurImage {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        OllamaDescripteur.class
    );
    private static final String BASE_URL = "http://localhost:11434";
    private static final String MODEL_NAME = "gemma4:e2b";
    private static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(2);

    private final AudiodescriptionAssistant assistant;

    /**
     * Charge le prompt et configure l'assistant réutilisé pour les descriptions.
     * La construction ne lance aucune requête vers Ollama.
     *
     * @throws DescriptionImpossibleException si le prompt ou la configuration est invalide
     */
    public OllamaDescripteur() throws DescriptionImpossibleException {
        try {
            assistant = createAssistant(loadMainPrompt());
        } catch (IOException | RuntimeException e) {
            throw new DescriptionImpossibleException(
                "Impossible d'initialiser le descripteur Ollama.",
                e
            );
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws DescriptionImpossibleException si l'image est absente ou vide,
     *         ou si Ollama ne fournit pas de réponse exploitable
     */
    @Override
    public Description decrire(Image image, String question)
        throws DescriptionImpossibleException {
        try {
            validateImage(image);
            String secondaryPrompt = question;
            if (secondaryPrompt == null || secondaryPrompt.isBlank()) {
                secondaryPrompt =
                    "Fournis une audiodescription générale de cette image.";
            }

            Result<String> result = assistant.describe(
                secondaryPrompt,
                ImageContent.from(image.getBase64EncodedImage(), "image/png")
            );
            for (var execution : result.toolExecutions()) {
                LOGGER.info(
                    "[Outil] {} → {}",
                    execution.request().name(),
                    execution.result()
                );
            }
            String answer = result.content();
            if (answer == null || answer.isBlank()) {
                throw new IllegalStateException(
                    "Ollama a renvoyé une réponse vide."
                );
            }

            return new Description(answer);
        } catch (RuntimeException e) {
            throw new DescriptionImpossibleException(
                "Impossible de générer la description avec Ollama.",
                e
            );
        }
    }

    /**
     * Vérifie la présence des données de l'image, sans décoder ni charger de fichier.
     *
     * @param image l'image à vérifier
     * @throws IllegalArgumentException si l'image est absente ou vide
     */
    private void validateImage(Image image) {
        if (image == null) {
            throw new IllegalArgumentException("Une image est obligatoire.");
        }
        String base64 = image.getBase64EncodedImage();
        if (base64 == null || base64.isBlank()) {
            throw new IllegalArgumentException(
                "L'image ne peut pas être vide."
            );
        }
    }

    /**
     * Configure le modèle, le prompt principal et l'exécution des outils.
     *
     * @param mainPrompt les instructions communes aux descriptions
     * @return l'assistant configuré, sans mémoire partagée entre les appels
     */
    private AudiodescriptionAssistant createAssistant(String mainPrompt) {
        OllamaChatModel model = OllamaChatModel.builder()
            .baseUrl(BASE_URL)
            .modelName(MODEL_NAME)
            .timeout(REQUEST_TIMEOUT)
            .build();

        return AiServices.builder(AudiodescriptionAssistant.class)
            .chatModel(model)
            .systemMessageProvider(memoryId -> mainPrompt)
            .tools(new DateTools())
            .toolArgumentsErrorHandler(
                ToolArgumentsErrorHandler.sendExceptionMessageToLlm()
            )
            .toolExecutionErrorHandler(
                ToolExecutionErrorHandler.failInvocationUnlessVisibleToLlm()
            )
            .build();
    }

    /**
     * Charge les instructions principales depuis les ressources de l'application.
     *
     * @return le prompt principal en UTF-8
     * @throws IOException si le fichier est absent, illisible ou vide
     */
    private String loadMainPrompt() throws IOException {
        String resource = "/audiodescription-prompt.txt";
        try (InputStream stream = getClass().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IOException(
                    "Le fichier audiodescription-prompt.txt est introuvable."
                );
            }
            String prompt = new String(
                stream.readAllBytes(),
                StandardCharsets.UTF_8
            );
            if (prompt.isBlank()) {
                throw new IOException("Le prompt principal est vide.");
            }
            return prompt;
        }
    }
}
