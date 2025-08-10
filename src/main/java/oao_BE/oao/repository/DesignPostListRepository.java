package oao_BE.oao.repository;

import oao_BE.oao.domain.DesignPost;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DesignPostListRepository extends JpaRepository<DesignPost, Long> {

    // 연관: user, aiProduct, aiProduct.images 한 번에 로딩
    @EntityGraph(attributePaths = {"user", "aiProduct", "aiProduct.images"})
    List<DesignPost> findBy();   // 조건 없음 = 전체 조회
}