package oao_BE.oao.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oao_BE.oao.service.DesignPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping("/api")
public class DesignPostController {

    private final DesignPostService designPostService;

    // 게시물 목록 가져오기
    @GetMapping("/post/list")
    public ResponseEntity<?> getDesignPostList() {
        return designPostService.getDesignPostList();
    }

    // 게시물 상세보기
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getDesignPost(@PathVariable Long postId) {
        return designPostService.getDesignPost(postId);
    }
}
