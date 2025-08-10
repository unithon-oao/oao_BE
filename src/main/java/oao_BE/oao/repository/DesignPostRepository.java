package oao_BE.oao.repository;

import oao_BE.oao.domain.DesignPost;
import oao_BE.oao.domain.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DesignPostRepository extends JpaRepository<DesignPost, Long> {
    @EntityGraph(attributePaths = {"user", "aiProduct", "aiProduct.images", "aiImage"})
    Optional<DesignPost> findByDesignPostId(Long designPostId);
}
