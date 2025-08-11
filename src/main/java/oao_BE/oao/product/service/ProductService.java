package oao_BE.oao.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oao_BE.oao.domain.Product;
import oao_BE.oao.product.dto.ProductDetailDTO;
import oao_BE.oao.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public List<Map<String, Object>> productList(String brand) {
        List<Product> products = productRepository.findByBrand(brand);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Product product : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", product.getProductId());
            map.put("productName", product.getProductName());
            map.put("brand", product.getBrand());
            map.put("productImage", product.getProductImage());
            map.put("productCategory", product.getProductCategory().toString());
            //map.put("productSize", product.getProductSize().toString());


            result.add(map);
        }

        return result;
    }

    public ProductDetailDTO productDetail(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Not Found"));
        return new ProductDetailDTO(
                product.getProductId(),
                product.getProductName(),
                product.getBrand(),
                product.getProductImage(),
                product.getProductCategory()
               // product.getProductSize()
        );
    }
}
