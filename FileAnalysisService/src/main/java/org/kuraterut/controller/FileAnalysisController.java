package org.kuraterut.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.kuraterut.model.AnalysisResult;
import org.kuraterut.service.AnalysisService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@Tag(name = "File Analysis Controller", description = "File analysis contoller of Antiplag HSE API")
public class FileAnalysisController {
    private final AnalysisService analysisService;

    @GetMapping("/{fileId}")
    @Operation(summary = "Get Analysis result", description = "Get analysis result about file by ID")
    @ApiResponse(responseCode = "200", description = "File analysis result")
    @ApiResponse(responseCode = "404", description = "File not found by ID")
    @ApiResponse(responseCode = "503", description = "File storage or analysis is unavailable")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<AnalysisResult> getAnalysisResult(@PathVariable("fileId") Long fileId) throws IOException {
        return ResponseEntity.ok(analysisService.analyzeFile(fileId));
    }

    @GetMapping("/wordcloud/{fileId}")
    @Operation(summary = "Get Wordcloud Image", description = "Get Wordcloud Image of file content by ID")
    @ApiResponse(responseCode = "200", description = "Wordcloud image")
    @ApiResponse(responseCode = "404", description = "Image not found")
    @ApiResponse(responseCode = "503", description = "File analysis is unavailable")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<byte[]> getImage(@PathVariable("fileId") Long fileId) throws IOException {

        byte[] imageBytes = analysisService.getWordCloudImage(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}