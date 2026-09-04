package com.sagark.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        // Original filename
        String originalFileName = file.getOriginalFilename();

        // Generate unique filename
        String randomId = UUID.randomUUID().toString();

        // Example:
        // image.jpg -> uuid.jpg
        String fileName = randomId.concat(
                originalFileName.substring(originalFileName.lastIndexOf("."))
        );

        // Create directory if it doesn't exist
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Correct file path
        Path filePath = Paths.get(path, fileName);

        // Copy file to destination
        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        System.out.println("Image uploaded: " + filePath);

        // Return saved filename
        return fileName;
    }
}
