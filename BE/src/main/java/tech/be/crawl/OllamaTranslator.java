package tech.be.crawl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Ollama REST API를 호출해 영문 텍스트를 한국어로 번역한다.
 * POST http://localhost:11434/api/generate
 */
@Component
public class OllamaTranslator {

    private final RestClient restClient;
    private final String model;

    public OllamaTranslator(
            @Value("${ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${ollama.model:gemma3:4b}") String model) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.model = model;
    }

    public String translate(String text) {
        if (text == null || text.isBlank()) return null;

        String prompt = "Translate the following text into natural Korean. Output only the translation:\n\n" + text;

        var request = Map.of("model", model, "prompt", prompt, "stream", false);

        try {
            var response = restClient.post()
                    .uri("/api/generate")
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            return response != null ? (String) response.get("response") : null;
        } catch (Exception e) {
            System.err.printf("[OllamaTranslator] 번역 실패: %s%n", e.getMessage());
            return null;
        }
    }
}
