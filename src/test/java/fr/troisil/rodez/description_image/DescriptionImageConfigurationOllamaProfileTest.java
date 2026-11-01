package fr.troisil.rodez.description_image;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.troisil.rodez.description_image.service.DescripteurImage;
import fr.troisil.rodez.description_image.service.OllamaDescripteur;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Vérifie que le bean descripteurImage est correctement configuré avec le profile 'ollama'.
 */
@SpringBootTest
@ActiveProfiles("ollama")
class DescriptionImageConfigurationOllamaProfileTest {

    @Autowired
    @Qualifier("descripteurImage")
    private DescripteurImage descripteurImage;

    @Test
    void descripteurImageBeanIsNotNull() {
        assertNotNull(
            descripteurImage,
            "Le bean descripteurImage ne doit pas être null"
        );
    }

    @Test
    void descripteurImageBeanIsOllamaDescripteur() {
        assertInstanceOf(
            OllamaDescripteur.class,
            descripteurImage,
            "Avec le profile 'ollama', le bean descripteurImage doit être une instance de OllamaDescripteur"
        );
    }
}
