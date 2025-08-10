package oao_BE.oao.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import oao_BE.oao.domain.AIProduct;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetDesignPostResponse {

    private Long postId;
    private String name;
    private Long userId;
    private List<String> aiProductImage;
}