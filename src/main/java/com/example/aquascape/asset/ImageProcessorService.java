package com.example.aquascape.asset;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class ImageProcessorService {

    @lombok.Value
    public static class ProcessedImage {
        byte[] data;
        String extension;
    }

    /**
     * Compresses and resizes an image.
     * 
     * @param imageFile The uploaded image file.
     * @param maxWidth Maximum width of the output image.
     * @param maxHeight Maximum height of the output image.
     * @param quality Quality from 0.0 to 1.0.
     * @return A byte array containing the compressed image data.
     */
    public ProcessedImage compressImage(MultipartFile imageFile, int maxWidth, int maxHeight, float quality) {
        String originalFilename = imageFile.getOriginalFilename();
        long originalSize = imageFile.getSize();
        String originalExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            originalExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }

        log.info("Compressing image: {} (Original size: {} bytes)", originalFilename, originalSize);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Thumbnails.of(imageFile.getInputStream())
                    .size(maxWidth, maxHeight)
                    .outputQuality(quality)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            byte[] compressedData = outputStream.toByteArray();
            
            if (compressedData.length >= originalSize) {
                log.info("Compressed size ({} bytes) is larger than or equal to original ({} bytes). Using original.", 
                        compressedData.length, originalSize);
                return new ProcessedImage(imageFile.getBytes(), originalExtension);
            }

            log.info("Successfully compressed image: {} (New size: {} bytes)", originalFilename, compressedData.length);
            return new ProcessedImage(compressedData, ".jpg");
            
        } catch (IOException e) {
            log.error("Failed to compress image: {}", originalFilename, e);
            try {
                return new ProcessedImage(imageFile.getBytes(), originalExtension);
            } catch (IOException ioException) {
                throw new RuntimeException("CRITICAL: Failed to read image bytes", ioException);
            }
        }
    }

    public ProcessedImage compressPreviewImage(MultipartFile imageFile) {
        return compressImage(imageFile, 512, 512, 0.75f); // Reduced quality slightly to 75%
    }
}
