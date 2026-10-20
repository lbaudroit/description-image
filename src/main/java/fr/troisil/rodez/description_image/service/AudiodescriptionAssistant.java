package fr.troisil.rodez.description_image.service;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.service.UserMessage;

/**
 * Interface implémentée par LangChain4j pour analyser une image avec Ollama.
 */
public interface AudiodescriptionAssistant {
    /**
     * Analyse une image selon les instructions principales et la demande complémentaire.
     *
     * @param question la demande complémentaire ou la consigne de description générale
     * @param image l'image à analyser
     * @return le texte de l'audiodescription
     */
    String describe(
        @UserMessage String question,
        @UserMessage ImageContent image
    );
}
