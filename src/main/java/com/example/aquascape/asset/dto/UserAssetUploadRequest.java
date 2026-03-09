package com.example.aquascape.asset.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserAssetUploadRequest {
    private String name;
    private MultipartFile glbFile;
    private MultipartFile previewImage;
}
