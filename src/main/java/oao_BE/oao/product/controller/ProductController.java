package oao_BE.oao.product.controller;

import lombok.RequiredArgsConstructor;
import oao_BE.oao.product.dto.ProductDetailDTO;
import oao_BE.oao.product.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    // 브랜드별 저장된 상품 리스트 가져오기
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> productList(@RequestParam("brand") String brand) {
        List<Map<String, Object>> listByBrand = productService.productList(brand);

        return ResponseEntity.ok(listByBrand);
    }

    // 상품 세부정보 가져오기
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailDTO> productDetail(@PathVariable Long productId) {
        ProductDetailDTO productDetailDTO = productService.productDetail(productId);

        return ResponseEntity.ok(productDetailDTO);
    }
}
