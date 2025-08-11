package oao_BE.oao.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oao_BE.oao.service.SuggestionService;
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
public class SuggestionController {

    private final SuggestionService suggestionService;

    // 디자이너 제안 목록
    @GetMapping("/suggestion/list/{aiProductId}")
    public ResponseEntity<?> getSuggestionList(@PathVariable Long aiProductId) {
        return suggestionService.getSuggestionList(aiProductId);
    }

    // 디자이너 제안 세부 정보
    @GetMapping("/suggestion/{designProductId}")
    public ResponseEntity<?> getSuggestion(@PathVariable Long designProductId) {
        return suggestionService.getSuggestion(designProductId);
    }
}
