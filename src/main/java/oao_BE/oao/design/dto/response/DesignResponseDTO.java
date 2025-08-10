package oao_BE.oao.design.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import oao_BE.oao.domain.AIProduct;

import java.util.List;

// ai 요청 후
@Data
@AllArgsConstructor
public class DesignResponseDTO {
    private List<DesignDTO> designs;

    @Data
    @AllArgsConstructor
    public static class DesignDTO {
        private Long aiProductId;
        private String aiProductImage;
        private String description;  // AI가 생성한 이미지에 대한 설명
    }

    // DB 에서 가져온 AIProduct 객체들을 응답 DTO 로 변환
    public static DesignResponseDTO fromEntities(List<AIProduct> products) {
        List<DesignDTO> designList = products.stream()
                .map(p -> new DesignDTO(p.getAiProductId(), p.getAiProductImage(), p.getDescription()))
                .toList();
        return new DesignResponseDTO(designList);
    }
}
