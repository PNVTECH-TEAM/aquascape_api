package com.example.aquascape.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobErrorCode;
import com.azure.storage.blob.models.BlobStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        return uploadFile(file.getBytes(), fileName);
    }

    public String uploadFile(byte[] content, String fileName) throws IOException {

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
        try (InputStream inputStream = new ByteArrayInputStream(content)) {
            blobClient.upload(inputStream, content.length, true);
        }

        return blobClient.getBlobUrl();
    }
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            
            String containerPath = "/" + containerName + "/";
            int index = fileUrl.indexOf(containerPath);
            if (index != -1) {
                String blobName = fileUrl.substring(index + containerPath.length());
                BlobClient blobClient = containerClient.getBlobClient(blobName);
                blobClient.deleteIfExists();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from Azure Storage: " + fileUrl, e);
        }
    }
}