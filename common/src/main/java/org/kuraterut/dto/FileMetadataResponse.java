package org.kuraterut.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "File metadata response")
public class FileMetadataResponse {
    @Schema(description = "Id", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "File name", example = "1.txt")
    private String fileName;

    @Schema(description = "Path to file", example = "uploads/1.txt")
    private String filePath;

    @Schema(description = "Date and time of uploading", example = "2025-05-19T23:00:00")
    private LocalDateTime uploadDate;


    @Schema(description = "File content hash")
    private String contentHash;

    public void prePersist() {
        uploadDate = LocalDateTime.now();
    }
}