package oao_BE.oao.design.controller;

import lombok.RequiredArgsConstructor;
import oao_BE.oao.design.dto.request.DesignRequestDTO;
import oao_BE.oao.design.dto.request.FinalDesignDTO;
import oao_BE.oao.design.dto.response.DesignResponseDTO;
import oao_BE.oao.design.service.DesignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/design")
public class DesignController {

    private final DesignService designService;

    // ai 이미지 생성
    @PostMapping("/generate")
    public ResponseEntity<DesignResponseDTO> generateDesign(@RequestBody DesignRequestDTO designRequestDTO) {
        DesignResponseDTO result = designService.generateDesign(designRequestDTO);
        return ResponseEntity.ok(result);
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

    // 선택된 이미지 직접 수정
    // 1. 텍스트 추가 수정
//    @PostMapping("/{aiImageId}/edit/text")
//    public ResponseEntity<DesignResponseDTO.DesignDTO> editImageWithText(
//            @PathVariable Long aiImageId,
//            @RequestBody ImageEditRequest request
//    ) {
//        return ResponseEntity.ok(designService.editImageWithText(aiImageId, request));
//    }
//
//    // 2. 이미지 업로드 수정
//    @PostMapping("/{aiImageId}/edit/upload")
//    public ResponseEntity<DesignResponseDTO.DesignDTO> editImageWithUpload(
//            @PathVariable Long aiImageId,
//            @RequestParam("file") MultipartFile file
//    ) {
//        return ResponseEntity.ok(designService.editImageWithUpload(aiImageId, file));
//    }
//
//    // 3. 직접 그리기 수정
//    @PostMapping("/{aiImageId}/edit/draw")
//    public ResponseEntity<DesignResponseDTO.DesignDTO> editImageWithDrawing(
//            @PathVariable Long aiImageId,
//            @RequestParam("file") MultipartFile file
//    ) {
//        return ResponseEntity.ok(designService.editImageWithDrawing(aiImageId, file));
//    }

    // 최종본 저장
    @PostMapping("/save")
    public ResponseEntity<?> saveDesign(@RequestBody FinalDesignDTO finalDesignDTO) {
        // TODO. isSelected 검증??

        designService.saveDesign(finalDesignDTO);
        return ResponseEntity.ok("Request saved successfully");
    }
}
