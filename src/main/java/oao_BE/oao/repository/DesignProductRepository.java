package oao_BE.oao.repository;

import oao_BE.oao.domain.DesignProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DesignProductRepository extends JpaRepository<DesignProduct, Long> {

    Optional<DesignProduct> findByAiProduct_AiProductId(Long aiProductId);
}
