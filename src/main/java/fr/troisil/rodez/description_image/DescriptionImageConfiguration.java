package fr.troisil.rodez.description_image;

import fr.troisil.rodez.description_image.domain.exceptions.DescriptionImpossibleException;
import fr.troisil.rodez.description_image.service.DescripteurImage;
import fr.troisil.rodez.description_image.service.FauxDescripteur;
import fr.troisil.rodez.description_image.service.OllamaDescripteur;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DescriptionImageConfiguration {

    @Profile({"default", "faux"})
    @Bean("descripteurImage")
    public DescripteurImage descripteurFactice() {
        return new FauxDescripteur();
    }

    @Profile("ollama")
    @Bean("descripteurImage")
    public DescripteurImage descripteurOllama() {
        try {
            return new OllamaDescripteur();
        } catch (DescriptionImpossibleException e) {
            return new FauxDescripteur();
        }
    }
}
