package oao_BE.oao.design.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AiImageDTO {
    private Long aiImageId;
    private String aiImage;
    private Boolean isSelected;
    private Long aiProductId;
}