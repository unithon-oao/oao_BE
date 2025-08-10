package oao_BE.oao.design.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditImageRequestDTO {
    private Long aiImageId;   // 수정할 AI 이미지 ID
    private String editPrompt; // 수정 요청 프롬프트
}

