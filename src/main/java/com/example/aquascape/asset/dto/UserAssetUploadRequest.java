package com.example.aquascape.asset.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserAssetUploadRequest {
    private String name;
    private String type;
    private MultipartFile glbFile;
    private MultipartFile previewImage;
}
