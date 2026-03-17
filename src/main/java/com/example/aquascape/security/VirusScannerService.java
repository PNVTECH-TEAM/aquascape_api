package com.example.aquascape.security;

import fi.solita.clamav.ClamAVClient;
import com.example.aquascape.exception.VirusDetectedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class VirusScannerService {

    private final ClamAVClient clamAVClient;

    public VirusScannerService(@Value("${clamav.host}") String host, 
                               @Value("${clamav.port}") int port) {
        this.clamAVClient = new ClamAVClient(host, port);
    }

    public void scanFile(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            log.info("Scanning file for viruses: {}", file.getOriginalFilename());
            
            byte[] response = clamAVClient.scan(is);
            String result = ClamAVClient.isCleanReply(response) ? "OK" : new String(response);

            if (!"OK".equals(result)) {
                log.error("Virus detected in file {}: {}", file.getOriginalFilename(), result);
                throw new VirusDetectedException("Malicious content detected: " + result);
            }
            
            log.info("File {} is clean.", file.getOriginalFilename());
        } catch (IOException e) {
            log.error("Error connecting to ClamAV for scanning", e);
            // Có thể cân nhắc cho phép qua nếu ClamAV chết, hoặc chặn hoàn toàn tùy policy
            // Ở đây tôi chọn throw lỗi hệ thống để đảm bảo an toàn tối đa
            throw new RuntimeException("Antivirus service is currently unavailable", e);
        }
    }
}
