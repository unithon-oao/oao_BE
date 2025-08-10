package oao_BE.oao.design.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oao_BE.oao.design.dto.request.DesignRequestDTO;
import oao_BE.oao.design.dto.response.DesignResponseDTO;
import oao_BE.oao.design.repository.DesignRepository;
import oao_BE.oao.domain.AIImage;
import oao_BE.oao.domain.AIProduct;
import oao_BE.oao.domain.Product;
import oao_BE.oao.domain.User;
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

        // 4. DB 저장 - User 임시 생성
        User user = User.builder()
                .userId(1L)
                .build();

        List<AIProduct> aiProducts = designs.stream()
                .map(d -> {
                    AIProduct aiProduct = AIProduct.builder()
                            .description(d.getDescription())
                            .prompt(designRequestDTO.getPrompt())
                            .product(product)
                            .user(user)
                            .build();

                    // AIImage 생성 (이미지 URL + 선택여부 false 기본값)
                    AIImage aiImage = AIImage.builder()
                            .aiImage(d.getAiProductImage())  // 이미지 URL 넣기
                            .isSelected(false)               // 기본 false
                            .aiProduct(aiProduct)            // 양방향 설정
                            .build();

                    // AIProduct의 images 리스트에 AIImage 추가 (양방향 관계 위해 리스트도 세팅)
                    aiProduct.setImages(List.of(aiImage));  // setImages 메서드가 있으면 사용, 없으면 직접 필드에 접근 불가

                    return aiProduct;
                })
                .toList();


        // 5. 응답 반환
        return new DesignResponseDTO(designs);

    }
}