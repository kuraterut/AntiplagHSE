package org.kuraterut.controller;

import lombok.RequiredArgsConstructor;
import org.kuraterut.dto.AnalysisRequest;
import org.kuraterut.model.AnalysisResult;
import org.kuraterut.service.AnalysisService;
import org.kuraterut.service.WordCloudService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class FileAnalysisController {
    private final AnalysisService analysisService;

    @GetMapping("/{fileId}")
    public ResponseEntity<AnalysisResult> getAnalysisResult(@PathVariable("fileId") Long fileId) throws IOException {
        return ResponseEntity.ok(analysisService.analyzeFile(fileId));
    }

    @GetMapping("/wordcloud/{fileId}")
    public ResponseEntity<byte[]> getImage(@PathVariable("fileId") Long fileId) throws IOException {

        byte[] imageBytes = analysisService.getWordCloudImage(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}