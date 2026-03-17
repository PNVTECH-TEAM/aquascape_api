package com.example.aquascape.security;

import com.example.aquascape.exception.InsecureFileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;

@Service
@Slf4j
public class FileSecurityService {

    private final Tika tika;

    public FileSecurityService() {
        try {
            this.tika = new Tika(TikaConfig.getDefaultConfig());
        } catch (Exception e) {
            log.error("Failed to initialize Tika with custom config", e);
            throw new RuntimeException("Security service initialization failed", e);
        }
    }

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
        "image/png", 
        "image/jpeg"
    );

    private static final Set<String> ALL_ALLOWED_TYPES = Set.of(
        "model/gltf-binary",
        "image/png",
        "image/jpeg"
    );

    public void validate(MultipartFile file) {
        String mimeType = detectMimeType(file);
        if (!ALL_ALLOWED_TYPES.contains(mimeType)) {
            throw new InsecureFileException("Unsafe file type detected: " + mimeType + ". Only GLB, PNG, and JPEG are allowed.");
        }
    }

    public void validateGlb(MultipartFile file) {
        String mimeType = detectMimeType(file);
        if (!"model/gltf-binary".equals(mimeType)) {
            throw new InsecureFileException("Invalid GLB file. Detected type: " + mimeType);
        }
    }

    public void validateImage(MultipartFile file) {
        String mimeType = detectMimeType(file);
        if (!ALLOWED_IMAGE_TYPES.contains(mimeType)) {
            throw new InsecureFileException("Invalid image file. Detected type: " + mimeType + ". Only PNG and JPEG are allowed.");
        }
    }

    private String detectMimeType(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String mimeType = null;

        if (originalFilename != null && originalFilename.toLowerCase().endsWith(".glb")) {
            if (isRealGlbFile(file)) {
                return "model/gltf-binary";
            }
        }

        try (InputStream inputStream = file.getInputStream()) {
            Metadata metadata = new Metadata();
            if (originalFilename != null) {
                metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, originalFilename);
            }
            mimeType = tika.detect(inputStream, metadata);
            log.info("Tika detected mimeType: {} for file: {}", mimeType, originalFilename);
            return mimeType;
        } catch (IOException e) {
            log.error("Tika detection failed", e);
            throw new RuntimeException("Error reading file for security scan", e);
        }
    }

    private boolean isRealGlbFile(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[4];
            int bytesRead = is.read(header);
            if (bytesRead < 4) return false;
            
            String magic = new String(header, StandardCharsets.US_ASCII);
            return "glTF".equals(magic);
        } catch (Exception e) {
            return false;
        }
    }
}