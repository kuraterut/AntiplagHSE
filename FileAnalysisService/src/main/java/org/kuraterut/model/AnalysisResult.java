package org.kuraterut.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Schema(description = "Analysis result response")
public class AnalysisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Analysis result ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "File ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long fileId;

    @Schema(description = "Count of symbols in file", example = "35")
    private int charCount;

    @Schema(description = "Count words in file", example = "35")
    private int wordCount;

    @Schema(description = "Count of paragraph", example = "35")
    private int paragraphCount;

    @Schema(description = "Word cloud URI", example = ".../wordcloud_1.png")
    private String wordCloudUri;

    @Column(columnDefinition = "TIMESTAMP")
    @Schema(description = "Date and time of analysing", example = "2025-05-19T23:00:00")
    private LocalDateTime analyzedAt;

    @PrePersist
    public void prePersist() {
        analyzedAt = LocalDateTime.now();
    }
}