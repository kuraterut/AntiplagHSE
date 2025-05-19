package org.kuraterut.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "files")
@Schema(description = "File metadata response")
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Id", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "File name", example = "1.txt")
    private String fileName;

    @Schema(description = "Path to file", example = "uploads/1.txt")
    private String filePath;

    @Schema(description = "Date and time of uploading", example = "2025-05-19T23:00:00")
    private LocalDateTime uploadDate;

    @Column(unique = true)
    @Schema(description = "File content hash")
    private String contentHash;

    @PrePersist
    public void prePersist() {
        uploadDate = LocalDateTime.now();
    }
}