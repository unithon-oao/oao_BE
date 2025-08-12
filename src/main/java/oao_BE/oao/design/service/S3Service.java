package oao_BE.oao.design.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;
    private final RestTemplate restTemplate;
    private final String bucketName = "your-s3-bucket-name";

    public String saveImageFromUrl(String dallEImageUrl) {
        // 1. DALL-E 이미지 다운로드
        byte[] imageBytes = restTemplate.getForObject(dallEImageUrl, byte[].class);

        // 2. S3에 업로드
        String fileName = "designs/" + UUID.randomUUID() + ".png";
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/png");

        try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
            s3Client.putObject(bucketName, fileName, inputStream, metadata);
        } catch (Exception e) {
            throw new RuntimeException("S3 이미지 업로드 실패", e);
        }

        // 3. S3 URL 반환
        return s3Client.getUrl(bucketName, fileName).toString();
    }
}