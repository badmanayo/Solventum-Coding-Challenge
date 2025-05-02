package com.example.shortlink;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testEncodeAndDecode() throws Exception {
        String originalUrl = "https://cxamplc.com/library/react";

        // Perform encode request
        MvcResult encodeResult = mockMvc.perform(post("/api/encode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"" + originalUrl + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        // Extract shortUrl from JSON
        String encodeBody = encodeResult.getResponse().getContentAsString();
        JsonNode encodeJson = objectMapper.readTree(encodeBody);
        String shortUrl = encodeJson.path("shortUrl").asText(null);
        assertThat(shortUrl).as("shortUrl should not be null").isNotNull();

        // Perform decode request
        MvcResult decodeResult = mockMvc.perform(post("/api/decode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shortUrl\": \"" + shortUrl + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        // Extract originalUrl from JSON
        String decodeBody = decodeResult.getResponse().getContentAsString();
        JsonNode decodeJson = objectMapper.readTree(decodeBody);
        String decodedUrl = decodeJson.path("originalUrl").asText(null);
        assertThat(decodedUrl).as("Decoded URL should match the original").isEqualTo(originalUrl);
    }
}
