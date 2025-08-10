package oao_BE.oao.product.repository;

import oao_BE.oao.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
