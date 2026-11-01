package fr.troisil.rodez.description_image.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ImageTest {

    @ParameterizedTest
    @ValueSource(strings = {"image/png", "image/jpeg", "image/gif"})
    void preservesContentAndMimeType(String mimeType) {
        Image image = new Image("aW1hZ2U=", mimeType);

        assertEquals("aW1hZ2U=", image.getBase64EncodedImage());
        assertEquals(mimeType, image.getMimeType());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void rejectsMissingOrBlankContent(String content) {
        assertThrows(IllegalArgumentException.class, () ->
            new Image(content, "image/png")
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "text/plain", "image/webp", "image/*"})
    void rejectsMissingOrUnsupportedMimeType(String mimeType) {
        assertThrows(IllegalArgumentException.class, () ->
            new Image("aW1hZ2U=", mimeType)
        );
    }
}
