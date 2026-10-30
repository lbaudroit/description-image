package fr.troisil.rodez.description_image.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

/**
 * Vérifie l'outil de date sans appeler Ollama.
 */
class DateToolsTest {

    @Test
    void returnsTodayInParis() {
        String expected = LocalDate.now(ZoneId.of("Europe/Paris")).toString();
        assertEquals(expected, new DateTools().today());
    }
}
