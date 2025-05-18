package org.kuraterut.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "files")
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String filePath;
    private LocalDateTime uploadDate;

    @Column(unique = true)  // Уникальность хеша для предотвращения дубликатов
    private String contentHash;  // SHA-256 хеш содержимого

    @PrePersist
    public void prePersist() {
        uploadDate = LocalDateTime.now();
    }
}