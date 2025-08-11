package oao_BE.oao.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oao_BE.oao.domain.AIImage;
import oao_BE.oao.domain.AIProduct;
import oao_BE.oao.domain.DesignPost;
import oao_BE.oao.domain.User;
import oao_BE.oao.dto.response.GetDesignPostListResponse;
import oao_BE.oao.dto.response.GetDesignPostResponse;
import oao_BE.oao.repository.DesignPostListRepository;
import oao_BE.oao.repository.DesignPostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DesignPostService {

    private final DesignPostListRepository designPostListRepository;
    private final DesignPostRepository designPostRepository;

    // 게시물 목록 가져오기
    public ResponseEntity<GetDesignPostListResponse> getDesignPostList() {
        List<DesignPost> posts = designPostListRepository.findAll();

        List<GetDesignPostResponse> items = posts.stream()
                .map(dp -> GetDesignPostResponse.builder()
                        .postId(dp.getDesignPostId())
                        .description(Optional.ofNullable(dp.getAiProduct())
                                .map(AIProduct::getDescription)
                                .orElse(null))
                        .userId(Optional.ofNullable(dp.getUser()).map(User::getUserId).orElse(null))
                        .aiProductImage(
                                Optional.ofNullable(dp.getAiProduct())
                                        .map(AIProduct::getImages)   // List<AIImage>
                                        .orElse(Collections.emptyList())
                                        .stream()
                                        .map(AIImage::getAiImage) // URL 문자열
                                        .filter(Objects::nonNull)
                                        .distinct()
                                        .toList()
                        )
                        .build())
                .toList();

        return ResponseEntity.ok(
                GetDesignPostListResponse.builder()
                        .posts(items)
                        .build()
        );
    }

    // 게시물 상세보기
    public ResponseEntity<GetDesignPostResponse> getDesignPost(Long postId) {
        DesignPost dp = designPostRepository.findByDesignPostId(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DesignPost not found: " + postId));

        AIProduct ap = dp.getAiProduct();

        GetDesignPostResponse item = GetDesignPostResponse.builder()
                .userId(Optional.ofNullable(dp.getUser()).map(User::getUserId).orElse(null))
                .aiProductId(Optional.ofNullable(ap).map(AIProduct::getAiProductId).orElse(null))
                .productId(Optional.ofNullable(ap).map(AIProduct::getProduct).map(p -> p.getProductId()).orElse(null))
                .description(Optional.ofNullable(ap).map(AIProduct::getDescription).orElse(null))
                .price(Optional.ofNullable(ap).map(AIProduct::getRequestPrice).orElse(null))
                .request(Optional.ofNullable(ap).map(AIProduct::getRequest).orElse(null))
                .build();

        return ResponseEntity.ok(item);
    }
}