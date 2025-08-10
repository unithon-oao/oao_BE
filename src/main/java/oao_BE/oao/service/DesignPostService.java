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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DesignPostService {

    private final DesignPostListRepository designPostRepository;

    // 게시물 목록 가져오기
    public ResponseEntity<GetDesignPostListResponse> getDesignPostList() {
        List<DesignPost> posts = designPostRepository.findAll();

        List<GetDesignPostResponse> items = posts.stream()
                .map(dp -> GetDesignPostResponse.builder()
                        .postId(dp.getDesignPostId())
                        .name(Optional.ofNullable(dp.getUser()).map(User::getName).orElse(null))
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
}
