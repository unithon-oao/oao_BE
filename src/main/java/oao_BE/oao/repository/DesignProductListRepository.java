package oao_BE.oao.repository;

import oao_BE.oao.domain.DesignPost;
import oao_BE.oao.domain.DesignProduct;

import java.util.List;
import java.util.Optional;

public interface DesignProductListRepository {
    List<DesignProduct> findBy();   // 조건 없음 = 전체 조회
}
