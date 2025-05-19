package org.kuraterut.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FileMetadataResponse {
    private Long id;

    private String fileName;
    private String filePath;
    private LocalDateTime uploadDate;

    private String contentHash;  // SHA-256 хеш содержимого

    public void prePersist() {
        uploadDate = LocalDateTime.now();
    }
}