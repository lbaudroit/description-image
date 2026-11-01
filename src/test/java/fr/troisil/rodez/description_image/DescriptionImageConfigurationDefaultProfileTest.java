package fr.troisil.rodez.description_image;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import fr.troisil.rodez.description_image.service.DescripteurImage;
import fr.troisil.rodez.description_image.service.FauxDescripteur;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Vérifie que le bean descripteurImage est correctement configuré avec le profile 'default'.
 */
@SpringBootTest
@ActiveProfiles("default")
class DescriptionImageConfigurationDefaultProfileTest {

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
    void descripteurImageBeanIsFauxDescripteur() {
        assertInstanceOf(
            FauxDescripteur.class,
            descripteurImage,
            "Avec le profile 'default', le bean descripteurImage doit être une instance de FauxDescripteur"
        );
    }
}
