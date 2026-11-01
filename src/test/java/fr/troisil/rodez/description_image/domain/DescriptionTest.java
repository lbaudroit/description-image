package fr.troisil.rodez.description_image.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class DescriptionTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t\n" })
    void rejectsMissingOrBlankDescription(String text) {
        assertThrows(IllegalArgumentException.class, () ->
            new Description(text)
        );
    }
}
