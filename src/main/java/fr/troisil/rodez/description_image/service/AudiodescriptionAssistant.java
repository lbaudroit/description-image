package fr.troisil.rodez.description_image.service;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.UserMessage;

/**
 * Interface implémentée par LangChain4j pour gérer les échanges et les appels d'outils.
 */
public interface AudiodescriptionAssistant {

    /**
     * Analyse une image, en utilisant les outils disponibles si nécessaire.
     *
     * @param question la demande complémentaire ou la consigne de description générale
     * @param image l'image à analyser
     * @return le texte final et les informations sur les outils exécutés
     */
    Result<String> describe(@UserMessage String question, @UserMessage ImageContent image);
}
