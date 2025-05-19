package org.kuraterut.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnalysisResultResponse {
    private Long id;

    private Long fileId;
    private int charCount;
    private int wordCount;
    private int paragraphCount;
    private String wordCloudUri;

    private LocalDateTime analyzedAt;

    public void prePersist() {
        analyzedAt = LocalDateTime.now();
    }
}
