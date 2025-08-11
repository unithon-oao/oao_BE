package oao_BE.oao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oao_BE.oao.domain.AIProduct;
import oao_BE.oao.domain.DesignProduct;
import oao_BE.oao.domain.Product;
import oao_BE.oao.domain.User;
import oao_BE.oao.dto.response.GetSuggestionListResponse;
import oao_BE.oao.dto.response.GetSuggestionResponse;
import oao_BE.oao.repository.DesignProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SuggestionService {

    private final DesignProductRepository designProductRepository;

    // 디자이너 제안 목록
    public ResponseEntity<GetSuggestionListResponse> getSuggestionList(Long aiProductId) {
        Optional<DesignProduct> products = designProductRepository.findByAiProduct_AiProductId(aiProductId);

        List<GetSuggestionResponse> items = products.stream()
                .map(dp -> GetSuggestionResponse.builder()
                        .designProductId(dp.getDesignProductId())
                        .designProductName(dp.getDesignProductName())
                        .price(dp.getPrice())
                        .description(dp.getDescription())
                        .userId(Optional.ofNullable(dp.getUser())
                                .map(User::getUserId)
                                .orElse(null))
                        .aiProductId(Optional.ofNullable(dp.getAiProduct())
                                .map(AIProduct::getAiProductId)
                                .orElse(null))
                        .designProductImage(
                                Optional.ofNullable(dp.getDesignProductImage())
                                        .map(List::of) // 단일 이미지 → List로
                                        .orElse(Collections.emptyList())
                        )
                        .createdAt(dp.getCreatedAt())
                        .build())
                .toList();

        return ResponseEntity.ok(
                GetSuggestionListResponse.builder()
                        .suggestion(items)
                        .build()
        );
    }

    // 디자이너 제안 세부 정보
    public ResponseEntity<GetSuggestionResponse> getSuggestion(Long designProductId) {
        Optional<DesignProduct> dpOpt = designProductRepository.findByDesignProductId(designProductId);
        if (dpOpt.isEmpty()) return ResponseEntity.notFound().build();

        DesignProduct dp = dpOpt.get();
        AIProduct ap = dp.getAiProduct();
        Product product = (ap != null) ? ap.getProduct() : null;

        GetSuggestionResponse item = GetSuggestionResponse.builder()
                .designProductId(dp.getDesignProductId())
                .designProductImage(Collections.singletonList(dp.getDesignProductImage()))
                .createdAt(dp.getCreatedAt())
                .userId(dp.getUser().getUserId())
                .aiProductId(ap != null ? ap.getAiProductId() : null)
                .description(ap != null ? ap.getDescription() : null)
                .price(ap != null ? ap.getRequestPrice() : null)
                .designProductName(dp.getDesignProductName())
                .build();

        return ResponseEntity.ok(item);
    }
}