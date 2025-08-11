package oao_BE.oao.design.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oao_BE.oao.design.dto.request.DesignRequestDTO;
import oao_BE.oao.design.dto.request.FinalDesignDTO;
import oao_BE.oao.design.dto.response.AiImageDTO;
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
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private final ImageStorageService imageStorageService;


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
//            // 이미지 URL을 서버에 다운로드 및 저장
//            String savedUrl;
//            try {
//                savedUrl = downloadAndSaveImage(design.getAiProductImage());
//            } catch (IOException e) {
//                throw new RuntimeException("이미지 저장 실패", e);
//            }
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

//        String savedUrl;
//        try {
//            savedUrl = downloadAndSaveImage(newDesign.getAiProductImage());
//        } catch (IOException e) {
//            throw new RuntimeException("이미지 저장 실패", e);
//        }

        // 3. 새 이미지 설명 생성
        String descriptionPrompt = String.format(
                "아래 이미지에 대한 간단하고 명확한 설명을 한 문장으로 작성해줘.\n이미지 URL: %s\n원본 프롬프트: %s",
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

    public AiImageDTO getDesign(Long aiImageId) {
        AIImage aiImage = aiImageRepository.findById(aiImageId)
                .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다."));
        return new AiImageDTO(
                aiImage.getAiImageId(),
                aiImage.getAiImage(),
                aiImage.getIsSelected(),
                aiImage.getAiProduct().getAiProductId()
        );
    }

    // 직접 수정
    // 1. 텍스트
    // 2. 이미지 업로드
    // 3. 직접 그리기
    public String updateEditedImage(Long aiImageId, MultipartFile editedImage) throws IOException {
        // 1. 기존 AIImage 찾기
        AIImage aiImage = aiImageRepository.findById(aiImageId)
                .orElseThrow(() -> new IllegalArgumentException("AIImage를 찾을 수 없습니다."));

        // 2. 파일 저장 (로컬 or S3)
        String savePath = saveFile(editedImage, aiImageId);

        // 3. DB에 새 이미지 경로 업데이트
        aiImage.setAiImage(savePath);
        aiImageRepository.save(aiImage);

        return savePath; // 프론트에서 바로 사용할 수 있는 URL or 경로
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    private String saveFile(MultipartFile file, Long aiImageId) throws IOException {
        String ext = getFileExtension(file.getOriginalFilename());
        if (ext.isEmpty()) {
            ext = "png";
        }

        String uniqueName = "edited_" + aiImageId + "_" + System.currentTimeMillis() + "." + ext;

        Path tempDir = Paths.get(System.getProperty("user.dir"), "temp");
        Files.createDirectories(tempDir);

        Path savePath = tempDir.resolve(uniqueName);

        try (var inputStream = file.getInputStream()) {
            Files.copy(inputStream, savePath);
        }

        return "http://localhost:8080/temp/" + uniqueName;
    }




    @Transactional
    public void saveDesign(FinalDesignDTO dto) {
        AIProduct aiProduct = designRepository.findById(dto.getAiProductId())
                .orElseThrow(() -> new RuntimeException("AIProduct not found"));

        aiProduct.setRequest(dto.getRequest());
        aiProduct.setRequestPrice(dto.getRequestPrice());

        designRepository.save(aiProduct);
    }

    // 이미지 URL → 서버에 저장
//    public String downloadAndSaveImage(String imageUrl) throws IOException {
//        BufferedImage image = ImageIO.read(new URL(imageUrl));
//        String ext = "png"; // 확장자 지정 또는 URL에서 추출해도 됨
//        String uniqueName = System.currentTimeMillis() + "." + ext;
//        Path savePath = Paths.get("temp").resolve(uniqueName);
//        Files.createDirectories(savePath.getParent());
//        ImageIO.write(image, ext, savePath.toFile());
//        // 저장 후 접근 가능한 URL 리턴 (웹서버가 temp 폴더 정적 리소스 매핑 되어 있어야 함)
//        return "http://localhost:8080/temp/" + uniqueName;
//    }

}