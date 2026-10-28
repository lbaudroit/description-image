package fr.troisil.rodez.description_image;

import fr.troisil.rodez.description_image.service.DescripteurImage;
import fr.troisil.rodez.description_image.service.FauxDescripteur;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DescriptionImageConfiguration {

    @Bean("descripteurImage")
    public DescripteurImage descripteurFactice() {
        return new FauxDescripteur();
    }
}
