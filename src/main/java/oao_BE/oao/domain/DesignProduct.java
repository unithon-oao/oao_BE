package oao_BE.oao.domain;

import jakarta.persistence.*;
import lombok.*;
import oao_BE.oao.domain.common.BaseEntity;
import oao_BE.oao.domain.enums.ProductStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DesignProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long designProductId;

    private String designProductImage;
    private Float price;
    private String description;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private String designProductName;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "ai_product_id", nullable = false)
    private AIProduct aiProduct;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
