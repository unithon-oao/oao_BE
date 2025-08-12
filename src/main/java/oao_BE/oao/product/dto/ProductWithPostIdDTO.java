package oao_BE.oao.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import oao_BE.oao.domain.enums.ProductCategory;

@Data
@Builder
@AllArgsConstructor
public class ProductWithPostIdDTO {
    private Long postId;
    private Long productId;
    private String productName;
    private String brand;
    private String productImage; // imageUrl을 productImage로 변경
    private ProductCategory productCategory;
}
