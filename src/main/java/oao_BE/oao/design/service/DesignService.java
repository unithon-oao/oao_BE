package oao_BE.oao.design.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oao_BE.oao.design.dto.request.DesignRequestDTO;
import oao_BE.oao.design.dto.response.DesignResponseDTO;
import oao_BE.oao.design.repository.DesignRepository;
import oao_BE.oao.domain.AIProduct;
import oao_BE.oao.domain.Product;
import oao_BE.oao.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DesignService {
    private final ProductRepository productRepository;
    private final DesignRepository designRepository;
    private final OpenAIImage openAIImage;

    @Value("${ai.openai.api-key}")
    private String apiKey;

    @Value("${ai.openai.url}")
    private String openAiUrl;

    @Transactional
    public DesignResponseDTO generateDesign(DesignRequestDTO designRequestDTO) {
        // 1. 캐시(DB) 확인
        List<AIProduct> cached = designRepository
                .findByPromptAndProduct_ProductId(designRequestDTO.getPrompt(), designRequestDTO.getProductId());
        if (!cached.isEmpty()) {
            return DesignResponseDTO.fromEntities(cached);
        }

        // 2. 상품 정보 조회
        Product product = productRepository.findById(designRequestDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 3. OpenAI 이미지 생성
        List<DesignResponseDTO.DesignDTO> designs = openAIImage.generateImages(
                designRequestDTO.getPrompt(), product.getProductImage()
        );

        // 4. DB 저장
        // 임의 user 객체 생성

        List<AIProduct> aiProducts = designs.stream()
                .map(d -> AIProduct.builder()
                        .aiProductImage(d.getAiProductImage())
                        .description(d.getDescription())
                        .prompt(designRequestDTO.getPrompt())
                        .product(product)
                        .user(null) // 로그인 기능 붙이면 채우기
                        .build()
                ).toList();

        designRepository.saveAll(aiProducts);

        // 5. 응답 반환
        return new DesignResponseDTO(designs);

    }
}