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
 * La demande de l'utilisateur est envoyée séparément, avec le type réel de l'image.
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
     */
    public OllamaDescripteur() {
        this(new DateTools());
    }

    /**
     * Configure le descripteur avec un outil fourni, notamment pour observer ses appels en test.
     *
     * @param dateTools l'outil de date à enregistrer
     */
    OllamaDescripteur(DateTools dateTools) {
        try {
            assistant = createAssistant(loadMainPrompt(), dateTools);
        } catch (RuntimeException e) {
            throw new IllegalStateException(
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
        if (image == null) {
            throw new IllegalArgumentException("Une image est obligatoire.");
        }
        String secondaryPrompt = question;
        if (secondaryPrompt == null || secondaryPrompt.isBlank()) {
            secondaryPrompt =
                "Fournis une audiodescription générale de cette image.";
        }
        ImageContent imageContent = ImageContent.from(
            image.getBase64EncodedImage(),
            image.getMimeType()
        );

        Result<String> result;
        try {
            result = assistant.describe(secondaryPrompt, imageContent);
        } catch (RuntimeException e) {
            throw new DescriptionImpossibleException(
                "Impossible de générer la description avec Ollama.",
                e
            );
        }
        if (
            result == null ||
            result.content() == null ||
            result.content().isBlank()
        ) {
            throw new DescriptionImpossibleException(
                "Ollama a renvoyé une réponse vide."
            );
        }
        for (var execution : result.toolExecutions()) {
            LOGGER.info(
                "[Outil] {} → {}",
                execution.request().name(),
                execution.result()
            );
        }
        return new Description(result.content());
    }



    /**
     * Configure le modèle, le prompt principal et l'exécution des outils.
     *
     * @param mainPrompt les instructions communes aux descriptions
     * @param dateTools l'outil de date disponible pour le modèle
     * @return l'assistant configuré, sans mémoire partagée entre les appels
     */
    private AudiodescriptionAssistant createAssistant(
        String mainPrompt,
        DateTools dateTools
    ) {
        OllamaChatModel model = OllamaChatModel.builder()
            .baseUrl(BASE_URL)
            .modelName(MODEL_NAME)
            .timeout(REQUEST_TIMEOUT)
            .build();

        return AiServices.builder(AudiodescriptionAssistant.class)
            .chatModel(model)
            .systemMessageProvider(memoryId -> mainPrompt)
            .tools(dateTools)
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
     * @throws IllegalStateException si la ressource de l'application est absente,
     *         illisible ou vide
     */
    private String loadMainPrompt() {
        String resource = "/audiodescription-prompt.txt";
        try (InputStream stream = getClass().getResourceAsStream(resource)) {
            if (stream == null) {
                throw new IllegalStateException(
                    "Le fichier audiodescription-prompt.txt est introuvable."
                );
            }
            String prompt = new String(
                stream.readAllBytes(),
                StandardCharsets.UTF_8
            );
            if (prompt.isBlank()) {
                throw new IllegalStateException(
                    "Le prompt principal est vide."
                );
            }
            return prompt;
        } catch (IOException e) {
            throw new IllegalStateException(
                "Impossible de lire le prompt de l'application.",
                e
            );
        }
    }
}
