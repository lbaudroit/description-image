package fr.troisil.rodez.description_image.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
public class DescriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testDescribeWithValidImage() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
            "fichier",
            "test-image.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test image content".getBytes()
        );

        mockMvc.perform(
            multipart("/api/describe")
                .file(imageFile)
                .param("question", "What is in this image?")
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json"));
    }

    @Test
    void testDescribeWithNoQuestion() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile(
            "fichier",
            "test-image.png",
                MediaType.IMAGE_JPEG_VALUE,
            "test image content".getBytes()
        );

        mockMvc.perform(
            multipart("/api/describe")
                .file(imageFile)
        )
        .andExpect(status().isOk());
    }

    @Test
    void testDescribeWithNoFile() throws Exception {
        mockMvc.perform(
            multipart("/api/describe")
                .param("question", "What is this?")
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void testDescribeWithInvalidFileType() throws Exception {
        MockMultipartFile textFile = new MockMultipartFile(
            "fichier",
            "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
            "not an image".getBytes()
        );

        mockMvc.perform(
            multipart("/api/describe")
                .file(textFile)
                .param("question", "Is this an image?")
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(containsString("Type de fichier non supporté")));
    }
}
