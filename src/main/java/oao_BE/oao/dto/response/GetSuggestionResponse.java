package oao_BE.oao.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oao_BE.oao.domain.DesignProduct;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetSuggestionResponse {

    private Long designProductId;
    private List<String> designProductImage;
    private String price;
    private String description;
    private String designProductName;
    private LocalDateTime createdAt;
    private Long aiProductId;
    private Long userId;

    public static GetSuggestionResponse from(DesignProduct d) {
        return GetSuggestionResponse.builder()
                .designProductId(d.getDesignProductId())
                .designProductImage(Collections.singletonList(d.getDesignProductImage()))
                .price(d.getPrice())
                .description(d.getDescription())
                .designProductName(d.getDesignProductName())
                .createdAt(d.getCreatedAt())
                .aiProductId(d.getAiProduct().getAiProductId())
                .userId(d.getUser().getUserId())
                .build();
    }
}
