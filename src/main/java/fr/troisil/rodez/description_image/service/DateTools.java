package fr.troisil.rodez.description_image.service;

import dev.langchain4j.agent.tool.Tool;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Outils de date que le modèle peut demander à l'application d'exécuter.
 */
public class DateTools {

    /**
     * Lit la date actuelle dans le fuseau horaire de Paris.
     *
     * @return la date au format ISO {@code yyyy-MM-dd}
     */
    @Tool("Retourne la date actuelle à Paris au format yyyy-MM-dd. "
        + "À utiliser avant de comparer une date de péremption à aujourd'hui.")
    public String today() {
        return LocalDate.now(ZoneId.of("Europe/Paris")).toString();
    }
}
