package com.example.aquascape.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class AzureStorageService {

    private final BlobServiceClient blobServiceClient;
    private final String containerName;

    public AzureStorageService(
            BlobServiceClient blobServiceClient,
            @Value("${azure.storage.container-name}") String containerName) {
        this.blobServiceClient = blobServiceClient;
        this.containerName = containerName;
    }

    public String uploadFile(MultipartFile file, String pathPrefix) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String fileName = pathPrefix + "/" + UUID.randomUUID().toString() + extension;

        BlobContainerClient containerClient;
        try {
            containerClient = blobServiceClient.createBlobContainer(containerName);
        } catch (BlobStorageException ex) {
            if (!ex.getErrorCode().equals(BlobErrorCode.CONTAINER_ALREADY_EXISTS)) {
                throw ex;
            }
            containerClient = blobServiceClient.getBlobContainerClient(containerName);
        }

        BlobClient blobClient = containerClient.getBlobClient(fileName);
        blobClient.upload(file.getInputStream(), file.getSize(), true);

        return blobClient.getBlobUrl();
    }
}
