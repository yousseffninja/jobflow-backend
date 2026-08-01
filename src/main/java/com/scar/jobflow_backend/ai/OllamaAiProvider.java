package com.scar.jobflow_backend.ai;

import com.scar.jobflow_backend.common.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.http.HttpHeaders;

import java.util.Map;

@Service
public class OllamaAiProvider implements AiProvider {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String providerUrl;
    private final String model;

    public OllamaAiProvider(
            @Value("${jobflow.ai.provider-url}") String providerUrl,
            @Value("${jobflow.ai.model}") String model
    ) {
        this.providerUrl = providerUrl;
        this.model = model;
    }

    @Override
    public String generate(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false
        );

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(
                    providerUrl + "/api/generate", request, String.class
            );

            JsonNode json = objectMapper.readTree(response);
            return json.get("response").asText();
        } catch (Exception e) {
            throw new BadRequestException("Ai generation failed: " + e.getMessage());
        }
    }
}
