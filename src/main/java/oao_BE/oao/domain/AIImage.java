package oao_BE.oao.domain;

import jakarta.persistence.*;
import lombok.*;
import oao_BE.oao.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ai_image")
public class AIImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aiImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_product_id", nullable = false)
    private AIProduct aiProduct;

    @Column(nullable = false)
    private String aiImage;

    @Column(nullable = false)
    private Boolean isSelected;
}
