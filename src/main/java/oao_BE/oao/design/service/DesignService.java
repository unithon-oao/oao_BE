package oao_BE.oao.design.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oao_BE.oao.design.dto.request.DesignRequestDTO;
import oao_BE.oao.design.dto.request.FinalDesignDTO;
import oao_BE.oao.design.dto.response.DesignResponseDTO;
import oao_BE.oao.design.repository.AiImageRepository;
import oao_BE.oao.design.repository.DesignRepository;
import oao_BE.oao.domain.AIImage;
import oao_BE.oao.domain.AIProduct;
import oao_BE.oao.domain.Product;
import oao_BE.oao.domain.User;
import oao_BE.oao.product.repository.ProductRepository;
import oao_BE.oao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DesignService {
    private final ProductRepository productRepository;
    private final DesignRepository designRepository;
    private final AiImageRepository aiImageRepository;
    private final UserRepository userRepository;
    private final OpenAIImage openAIImage;
    private final OpenAIDescription openAIDescription;

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

        // 4. 각 이미지에 대한 설명 생성 (OpenAI 텍스트 API 별도 호출)
        List<String> descriptions = new ArrayList<>();
        for (DesignResponseDTO.DesignDTO design : designs) {
            String promptForDescription = String.format(
                    "아래 이미지에 대한 간단하고 명확한 설명을 작성해줘.\n" +
                            "이미지 URL: %s\n" +
                            "원본 프롬프트: %s",
                    design.getAiProductImage(), designRequestDTO.getPrompt()
            );
            String description = openAIDescription.generateDescription(promptForDescription);
            design.setDescription(description);  // DTO의 description 필드 직접 수정
        }

        // 5. DB 저장 - User 임시 생성
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


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
                    aiProduct.setImages(new ArrayList<>());
                    aiProduct.getImages().add(aiImage);

                    return aiProduct;
                })
                .toList();

        List<AIProduct> savedProducts = designRepository.saveAll(aiProducts);
        return DesignResponseDTO.fromEntities(savedProducts);

    }

    public DesignResponseDTO.DesignDTO regenerateDesign(DesignRequestDTO designRequestDTO, Long aiProductId) {
        // 1. 기존 AIProduct 조회
        AIProduct aiProduct = designRepository.findById(aiProductId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 AIProduct입니다."));

        // 2. OpenAI 이미지 1개 생성 요청
        List<DesignResponseDTO.DesignDTO> newDesigns = openAIImage.generateImages(designRequestDTO.getPrompt(), aiProduct.getProduct().getProductImage());

        if (newDesigns.isEmpty()) {
            throw new RuntimeException("이미지 재생성 실패");
        }

        DesignResponseDTO.DesignDTO newDesign = newDesigns.get(0);  // 1개만 생성했다고 가정

        // 3. 새 이미지 설명 생성
        String descriptionPrompt = String.format(
                "아래 이미지에 대한 간단하고 명확한 설명을 작성해줘.\n이미지 URL: %s\n원본 프롬프트: %s",
                newDesign.getAiProductImage(), designRequestDTO.getPrompt()
        );
        String newDescription = openAIDescription.generateDescription(descriptionPrompt);

        // 4. AIProduct, AIImage 수정 (기존 엔티티 업데이트)
        aiProduct.setDescription(newDescription);

        // 기존 AIImage가 여러개일 수 있으니 대표 이미지(예: 첫 번째) 교체
        AIImage aiImage = aiProduct.getImages().get(0);
        aiImage.setAiImage(newDesign.getAiProductImage());

        // 5. 저장
        designRepository.save(aiProduct);  // cascade 옵션에 따라 AIImage도 저장됨

        // 6. 변경된 정보 DTO로 반환
        return new DesignResponseDTO.DesignDTO(
                aiProduct.getAiProductId(),
                aiImage.getAiImage(),
                newDescription
        );
    }

    @Transactional
    public void selectDesign(Long aiImageId) {
        AIImage aiImage = aiImageRepository.findById(aiImageId)
                .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));

        // 선택한 이미지 true
        aiImage.setIsSelected(true);
    }

    @Transactional
    public void saveDesign(FinalDesignDTO dto) {
        AIProduct aiProduct = designRepository.findById(dto.getAiProductId())
                .orElseThrow(() -> new RuntimeException("AIProduct not found"));

        aiProduct.setRequest(dto.getRequest());
        aiProduct.setRequestPrice(dto.getRequestPrice());

        designRepository.save(aiProduct);
    }
}