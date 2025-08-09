package oao_BE.oao.domain;

import jakarta.persistence.*;
import lombok.*;
import oao_BE.oao.domain.DesignProduct;
import oao_BE.oao.domain.Product;
import oao_BE.oao.domain.User;
import oao_BE.oao.domain.common.BaseEntity;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ai_product")
public class AIProduct extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aiProductId;

    private String aiProductImage;
    private String prompt;
    private String description;
    private String request;
    private Float requestPrice;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "aiProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DesignProduct> designProducts;
}
