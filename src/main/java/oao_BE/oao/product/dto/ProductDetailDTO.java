package oao_BE.oao.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import oao_BE.oao.domain.enums.ProductCategory;
import oao_BE.oao.domain.enums.ProductSize;

@Data
@AllArgsConstructor
public class ProductDetailDTO {
    private Long productId;
    private String productName;
    private String brand;
    private String productImage;
    private ProductCategory productCategory;
    //private ProductSize productSize;
}
