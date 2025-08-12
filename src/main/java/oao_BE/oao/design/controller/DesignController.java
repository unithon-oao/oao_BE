package oao_BE.oao.design.controller;

import lombok.RequiredArgsConstructor;
import oao_BE.oao.design.dto.request.DesignRequestDTO;
import oao_BE.oao.design.dto.request.FinalDesignDTO;
import oao_BE.oao.design.dto.response.AiImageDTO;
import oao_BE.oao.design.dto.response.DesignResponseDTO;
import oao_BE.oao.design.service.DesignService;
import oao_BE.oao.product.dto.ProductDetailDTO;
import oao_BE.oao.product.dto.ProductWithPostIdDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/design")
public class DesignController {

    private final DesignService designService;

    // ai 이미지 생성
    @PostMapping("/generate")
    public ResponseEntity<DesignResponseDTO> generateDesign(@RequestBody DesignRequestDTO designRequestDTO) throws IOException {
        DesignResponseDTO result = designService.generateDesign(designRequestDTO);
        return ResponseEntity.ok(result);
    }

    // ai 이미지 뒷면 생성
    @PostMapping("/generateBack")
    public ResponseEntity<Map<String, Object>> generateBack(@RequestBody Map<String, Long> request) throws IOException {
        Long aiProductId = request.get("aiProductId");
        DesignResponseDTO.DesignDTO result = designService.generateBack(aiProductId);

        Map<String, Object> response = new HashMap<>();
        response.put("aiImageBackURL", result.getAiProductImage());
        response.put("aiProductId",aiProductId);
        return ResponseEntity.ok(response);
    }

    // 선택된 ai 이미지 재생성
    @PostMapping("/regenerate/{aiProductId}")
    public ResponseEntity<DesignResponseDTO.DesignDTO> regenerateDesign(@PathVariable Long aiProductId, @RequestBody DesignRequestDTO designRequestDTO) {
        DesignResponseDTO.DesignDTO result = designService.regenerateDesign(designRequestDTO, aiProductId);
        return ResponseEntity.ok(result);
    }

    // 생성된 이미지 선택
    @PatchMapping("/select/{aiProductId}")
    public ResponseEntity<?> selectDesign(@PathVariable Long aiProductId) {
        designService.selectDesign(aiProductId);
        return ResponseEntity.ok().build();
    }

    // 특정 디자인 선택
    @GetMapping("/{aiProductId}")
    public ResponseEntity<AiImageDTO> getDesign(@PathVariable Long aiProductId) {
        AiImageDTO aiImageDTO = designService.getDesign(aiProductId);
        return ResponseEntity.ok(aiImageDTO);
    }


    // 최종본 저장
    @PostMapping("/save")
    public ResponseEntity<?> saveDesign(@RequestBody FinalDesignDTO finalDesignDTO) {
        ProductWithPostIdDTO productWithPostIdDTO = designService.saveDesign(finalDesignDTO);
        return ResponseEntity.ok(productWithPostIdDTO);
    }
}
