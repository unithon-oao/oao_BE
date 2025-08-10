package oao_BE.oao.domain;

import jakarta.persistence.*;
import lombok.*;
import oao_BE.oao.domain.common.BaseEntity;
import oao_BE.oao.domain.enums.ProductStatus;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "design_product",
        indexes = {
                @Index(name = "ix_design_product_post", columnList = "design_post_id"),
                @Index(name = "ix_design_product_designer", columnList = "user_id"),
                @Index(name = "ix_design_product_status", columnList = "status")
        })
public class DesignProduct extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long designProductId;

    // 단일 대표 이미지(썸네일). 실제 이미지는 별도 테이블로 다중 저장
    private String designProductImage;

    private Float price;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private String designProductName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "design_post_id", nullable = false)
    private DesignPost designPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_product_id", nullable = false)
    private AIProduct aiProduct;

    // 디자이너
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "designProduct", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesignProductImage> images;
}
