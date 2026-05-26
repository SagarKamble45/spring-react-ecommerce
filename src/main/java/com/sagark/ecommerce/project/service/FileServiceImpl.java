package com.sagark.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        //get the fileNames or current / original file
        String originalFileName = file.getOriginalFilename();
        // Generate a Unique file name file
        String randomId = UUID.randomUUID().toString();

        // ex:- mat.jpg --> 1234(randomID) then --> 1234.jpg
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.pathSeparator + fileName;

        // check if path exist and create
        File folder = new File(path);
        if (!folder.exists()){
            folder.mkdir();
        }
        // Upload to Server
        Files.copy(file.getInputStream(), Path.of(filePath));

        // return filename
        return fileName;
    }
}
