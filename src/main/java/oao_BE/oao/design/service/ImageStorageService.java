package oao_BE.oao.design.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
@Transactional
public class ImageStorageService {
    private final Path storageDir = Paths.get("temp");  // 프로젝트 기준 상대경로 or 절대경로 설정

    public ImageStorageService() throws IOException {
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
    }

    // 이미지 바이트 저장 후 URL 리턴 (포트나 도메인 하드코딩 가능)
    public String saveImage(byte[] imageBytes, String filename) throws IOException {
        String ext = "png"; // 기본 확장자

        if (filename != null && filename.contains(".")) {
            ext = filename.substring(filename.lastIndexOf('.') + 1);
        }

        String uniqueName = System.currentTimeMillis() + "." + ext;  // 확장자 포함
        Path target = storageDir.resolve(uniqueName);
        Files.write(target, imageBytes);

        return "http://localhost:8080/temp/" + uniqueName;
    }
}
