package org.kuraterut.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnalysisRequest {
    private Long fileId;          // ID файла для анализа
    private Long compareWithId;   // ID файла для сравнения (опционально)
}