package oao_BE.oao.domain;

import jakarta.persistence.*;
import lombok.*;
import oao_BE.oao.domain.common.BaseEntity;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "design_post",
        indexes = {
                @Index(name = "ix_design_post_user", columnList = "user_id"),
                @Index(name = "ix_design_post_ai_product", columnList = "ai_product_id"),
                @Index(name = "ix_design_post_ai_image", columnList = "ai_image_id")
        })
public class DesignPost extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long designPostId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_product_id", nullable = false)
    private AIProduct aiProduct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ai_image_id", nullable = false)
    private AIImage aiImage; // 사용자가 고른 1장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 게시물 작성자

    // 게시물에 달린 리디자인들
    @Builder.Default
    @OneToMany(mappedBy = "designPost", fetch = FetchType.LAZY)
    private List<DesignProduct> designProducts = new ArrayList<>();
}