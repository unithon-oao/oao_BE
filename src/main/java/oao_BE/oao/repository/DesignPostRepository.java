package oao_BE.oao.repository;

import oao_BE.oao.domain.DesignPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DesignPostRepository extends JpaRepository<DesignPost, Long> {

    Optional<DesignPost> findByDesignPostId(Long designPostId);
}
