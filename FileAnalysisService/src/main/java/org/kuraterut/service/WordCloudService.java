package org.kuraterut.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kuraterut.feign.FileStorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class WordCloudService {
    private static final String QUICKCHART_API = "https://quickchart.io/wordcloud";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;



    public WordCloudService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) throws IOException {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;

    }

    public byte[] generateWordCloud(String text) {
        try {
            // Создаем JSON запрос
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text", text);
            requestBody.put("width", 800);
            requestBody.put("height", 600);
            requestBody.put("format", "png");
            requestBody.put("removeStopwords", true);
            requestBody.put("maxWords", 100);

            // Устанавливаем заголовки
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Формируем запрос
            HttpEntity<String> request = new HttpEntity<>(
                    objectMapper.writeValueAsString(requestBody),
                    headers
            );

            // Отправляем запрос
            ResponseEntity<byte[]> response = restTemplate.postForEntity(
                    QUICKCHART_API,
                    request,
                    byte[].class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw new RuntimeException("Failed to generate word cloud. Status: " + response.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Error calling QuickChart API: " + e.getMessage(), e);
        }
    }
}