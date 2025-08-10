package oao_BE.oao.design.service;

import lombok.RequiredArgsConstructor;
import oao_BE.oao.design.dto.response.DesignResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OpenAIImage {

    private final RestTemplate restTemplate;

    @Value("${ai.openai.api-key}")
    private String apiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/images/generations";

    public List<DesignResponseDTO.DesignDTO> generateImages(String prompt, String productImage) {
        // 프롬프트에 상품 이미지 주소를 붙여서 보낼 수도 있음
        String combinedPrompt = prompt + " on " + productImage;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        List<DesignResponseDTO.DesignDTO> results = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Map<String, Object> requestBody = Map.of(
                    "model", "dall-e-3",
                    "prompt", combinedPrompt,
                    "n", 1,               // 1개씩 생성
                    "size", "1024x1024"
            );


            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    OPENAI_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("OpenAI API 호출 실패: " + response.getStatusCode());
            }

            List<Map<String, String>> dataList = (List<Map<String, String>>) response.getBody().get("data");

            dataList.forEach(data -> {
                String imageUrl = data.get("url");
                results.add(new DesignResponseDTO.DesignDTO(null, imageUrl, "AI generated design"));
            });
        }

        return results;
    }
}
