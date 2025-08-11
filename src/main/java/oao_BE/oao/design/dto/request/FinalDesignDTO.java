package oao_BE.oao.design.dto.request;

import lombok.Data;
import oao_BE.oao.domain.AIImage;

@Data
public class FinalDesignDTO {
    private String request; // 요청 메세지
    private Float requestPrice;
    private Long aiProductId; // 선택된 이미지
    private String story;
    private String aiProductName;

}
