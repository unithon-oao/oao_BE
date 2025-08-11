package oao_BE.oao.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oao_BE.oao.domain.DesignPost;
import oao_BE.oao.domain.User;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetDesignPostResponse {

    private Long postId;
    private String description;
    private Long userId;
    private List<String> aiProductImage;

    private Long aiProductId;
    private Long productId;
    private String price;
    private String request;

    public static GetDesignPostResponse from(DesignPost d) {
        return GetDesignPostResponse.builder()
                .userId(d.getUser().getUserId())
                .aiProductId(d.getAiProduct().getAiProductId())
                .productId(d.getAiProduct().getProduct().getProductId())
                .description(d.getAiProduct().getDescription())
                .price(d.getAiProduct().getRequestPrice())
                .request(d.getAiProduct().getRequest())
                .build();
    }
}