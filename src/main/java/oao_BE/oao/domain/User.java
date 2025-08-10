package oao_BE.oao.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;
    private String introduction;
    private String profile;
    private String email;
    private String portfolio;
    private String field;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<AIProduct> aiProducts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<DesignProduct> designProducts;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<DesignPost> designPosts;
}