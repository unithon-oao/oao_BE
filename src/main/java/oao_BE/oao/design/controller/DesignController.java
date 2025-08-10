package oao_BE.oao.design.controller;

import lombok.RequiredArgsConstructor;
import oao_BE.oao.design.dto.request.DesignRequestDTO;
import oao_BE.oao.design.dto.response.DesignResponseDTO;
import oao_BE.oao.design.service.DesignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/design")
public class DesignController {

    private final DesignService designService;

    @PostMapping("/generate")
    public ResponseEntity<DesignResponseDTO> generateDesign(@RequestBody DesignRequestDTO designRequestDTO) {
        DesignResponseDTO result = designService.generateDesign(designRequestDTO);
        return ResponseEntity.ok(result);
    }
}
