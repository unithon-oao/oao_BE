package oao_BE.oao.design.service;

import lombok.RequiredArgsConstructor;
import oao_BE.oao.design.dto.response.DesignResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
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
    private static final String GPT4O_URL = "https://api.openai.com/v1/chat/completions";
    private static final String DALLE3_URL = "https://api.openai.com/v1/images/generations";

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


    // --- GPT-4o Vision API와 DALL-E 3 API를 활용하는 새로운 메서드 ---

    // 1. GPT-4o Vision API로 이미지 설명을 얻는 메서드
    public String describeImageWithVision(String imageUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of("type", "text", "text", "이 사진에 있는 주요 상품에 대해 자세히 설명해줘. 상품의 종류, 색상, 형태, 질감, 그리고 배경을 중심으로 묘사해줘."),
                                        Map.of("type", "image_url", "image_url", Map.of("url", imageUrl))
                                )
                        )
                ),
                "max_tokens", 300
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(GPT4O_URL, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("GPT-4o API 호출 실패: " + response.getStatusCode());
            }

            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");
        } catch (Exception e) {
            throw new RuntimeException("이미지 설명 생성 실패", e);
        }
    }

    // 2. 이미지 설명과 사용자 프롬프트를 결합해 DALL-E 3로 이미지를 생성하는 메서드
    public List<DesignResponseDTO.DesignDTO> generateImagesWithDescription(String imageDescription, String userPrompt) {
        // 1. 최종 프롬프트 구성 (이 부분은 반복문 밖에서 한 번만 하면 됩니다.)
        String finalPrompt = String.format(
                "%s. 그리고 이 상품에 '%s' 디자인을 적용해줘. 디자인이 상품의 질감을 따르도록 해.",
                imageDescription,
                userPrompt
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        List<DesignResponseDTO.DesignDTO> results = new ArrayList<>();

        // 2. 3개의 이미지를 생성하기 위해 API 호출을 3번 반복합니다.
        for (int i = 0; i < 3; i++) {
            Map<String, Object> requestBody = Map.of(
                    "model", "dall-e-3",
                    "prompt", finalPrompt,
                    "n", 1, // DALL-E 3는 한 번에 1개만 생성 가능
                    "size", "1024x1024"
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            try {
                ResponseEntity<Map> response = restTemplate.exchange(
                        DALLE3_URL,
                        HttpMethod.POST,
                        entity,
                        Map.class
                );

                if (response.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException("DALL-E 3 API 호출 실패: " + response.getStatusCode());
                }

                List<Map<String, String>> dataList = (List<Map<String, String>>) response.getBody().get("data");

                // 응답으로 받은 이미지 URL을 results 리스트에 추가합니다.
                dataList.forEach(data -> {
                    String imageUrl = data.get("url");
                    results.add(new DesignResponseDTO.DesignDTO(null, imageUrl, "AI generated design"));
                });
            } catch (Exception e) {
                // API 호출 실패 시 로그를 남기고 다음 반복으로 넘어갈 수 있도록 처리
            }
        }

        return results;
    }

    // 3. 뒷면 생성
    /**
     * 앞면 이미지 분석 결과를 바탕으로 뒷면 디자인을 생성합니다.
     * @param frontImageDescription GPT-4o가 분석한 앞면 이미지 설명
     * @return 생성된 뒷면 디자인 정보 DTO
     */
    public DesignResponseDTO.DesignDTO generateBackDesignWithDalle3(String frontImageDescription) {
        // 1. DALL-E 3에게 보낼 최종 프롬프트 구성
        // 명확하게 뒷면만 생성하도록 지시하는 영어 프롬프트
        String finalPrompt = String.format(
                "Generate a photorealistic image that exclusively shows the back view of the product. " +
                        "The product's front side is described as: '%s'. " +
                        "The back design should be simple, complementary to the front, and consistent in style and background with the original product.",
                frontImageDescription
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", "dall-e-3",
                "prompt", finalPrompt,
                "n", 1,
                "size", "1024x1024"
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    DALLE3_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("DALL-E 3 API 호출 실패: " + response.getStatusCode());
            }

            List<Map<String, String>> dataList = (List<Map<String, String>>) response.getBody().get("data");
            String imageUrl = dataList.get(0).get("url");

            return new DesignResponseDTO.DesignDTO(null, imageUrl, "AI generated back design");
        } catch (Exception e) {
            throw new RuntimeException("뒷면 디자인 생성 실패", e);
        }
    }
}
