package com.example.aquascape.asset;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Slf4j
public class GltfProcessorService {

    /**
     * Compresses a GLB file using gltf-pipeline with Draco compression.
     * 
     * @param glbFile The uploaded GLB file.
     * @return A byte array containing the compressed GLB data, or the original data if compression fails.
     */
    public byte[] compressGlb(MultipartFile glbFile) {
        String originalFilename = glbFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".glb")) {
            log.warn("File {} is not a GLB file, skipping compression.", originalFilename);
            try {
                return glbFile.getBytes();
            } catch (IOException e) {
                throw new RuntimeException("Failed to read original GLB file bytes", e);
            }
        }

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("gltf-pipeline-" + UUID.randomUUID());
            Path inputPath = tempDir.resolve("input.glb");
            Path outputPath = tempDir.resolve("output.glb");

            glbFile.transferTo(inputPath.toFile());

            ProcessBuilder processBuilder = new ProcessBuilder(
                "gltf-pipeline", 
                "-i", inputPath.toString(), 
                "-o", outputPath.toString(), 
                "-d"
            );
            
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug("gltf-pipeline: {}", line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0 && Files.exists(outputPath)) {
                log.info("Successfully compressed GLB file: {} (Original size: {} bytes, New size: {} bytes)", 
                    originalFilename, glbFile.getSize(), Files.size(outputPath));
                return Files.readAllBytes(outputPath);
            } else {
                log.error("gltf-pipeline failed with exit code {}. Falling back to original file.", exitCode);
                return glbFile.getBytes();
            }

        } catch (Exception e) {
            log.error("Error during GLB compression for {}. Falling back to original file.", originalFilename, e);
            try {
                return glbFile.getBytes();
            } catch (IOException ioException) {
                throw new RuntimeException("Failed to read original GLB file bytes after compression error", ioException);
            }
        } finally {
            // Cleanup
            if (tempDir != null) {
                deleteDirectory(tempDir.toFile());
            }
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}
