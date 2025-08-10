package oao_BE.oao.domain;

import jakarta.persistence.*;
import lombok.*;
import oao_BE.oao.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "design_product_image")
public class DesignProductImage extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long designProductImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "design_product_id", nullable = false)
    private DesignProduct designProduct;

    @Column(nullable = false)
    private String designProductImage; // URL

    private Integer sortOrder;
}
