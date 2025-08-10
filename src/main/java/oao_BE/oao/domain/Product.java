package oao_BE.oao.domain;

import jakarta.persistence.*;
import lombok.*;
import oao_BE.oao.domain.enums.ProductCategory;
import oao_BE.oao.domain.enums.ProductSize;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String productName;
    private String brand;
    private String productImage;

    @Enumerated(EnumType.STRING)
    private ProductCategory productCategory;

    @Enumerated(EnumType.STRING)
    private ProductSize productSize;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<AIProduct> aiProducts;
}