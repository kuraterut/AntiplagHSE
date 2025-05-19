package org.kuraterut.service;

import lombok.RequiredArgsConstructor;
import org.kuraterut.exception.CalculatingFileHashException;
import org.kuraterut.exception.FileNotFoundException;
import org.kuraterut.dto.FileMetadataResponse;
import org.kuraterut.model.FileMetadata;
import org.kuraterut.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final FileMetadataRepository repository;

    @Value("${file.storage.path}")
    private String storagePath;

    public FileMetadata saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(storagePath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String hash = calculateFileHash(file.getBytes());
        Optional<FileMetadata> optionalFileMetadata = repository.findByContentHash(hash);
        if (optionalFileMetadata.isPresent()) {
            return optionalFileMetadata.get();
        }

        String fileName = file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        FileMetadata metadata = new FileMetadata();
        metadata.setFileName(fileName);
        metadata.setFilePath(filePath.toString());
        metadata.setContentHash(hash);

        return repository.save(metadata);
    }

    public byte[] getFile(Long fileId) throws IOException {
        FileMetadata metadata = repository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));
        return Files.readAllBytes(Paths.get(metadata.getFilePath()));
    }

    private String calculateFileHash(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content);
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new CalculatingFileHashException("Error calculating file hash: " + e.getMessage());
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}