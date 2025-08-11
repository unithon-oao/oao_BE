package oao_BE.oao.design.repository;

import oao_BE.oao.domain.AIProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DesignRepository extends JpaRepository<AIProduct,Long> {
    // 간단 조회 (프롬프트+상품)
    List<AIProduct> findByPromptAndProduct_ProductId(String prompt, Long productId);
}
