package org.kuraterut.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Analysis result response")
public class AnalysisResultResponse {

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

    @Schema(description = "Date and time of analysing", example = "2025-05-19T23:00:00")
    private LocalDateTime analyzedAt;

    public void prePersist() {
        analyzedAt = LocalDateTime.now();
    }
}
