package org.kuraterut.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kuraterut.dto.FileMetadataResponse;
import org.kuraterut.model.FileMetadata;
import org.kuraterut.service.FileStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Storage Controller", description = "File storage Contoller of Antiplag HSE API")
public class FileStorageController {
    private final FileStorageService storageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file", description = "Upload file and return file info")
    @ApiResponse(responseCode = "200", description = "File info (id, location etc.)")
    @ApiResponse(responseCode = "503", description = "File storage is unavailable")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<FileMetadata> uploadFile(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        log.info("Content-Type: {}", file.getContentType());
        log.info("File received: {} ({} bytes)", file.getOriginalFilename(), file.getSize());
        return ResponseEntity.ok(storageService.saveFile(file));
    }


    @GetMapping("/{fileId}")
    @Operation(summary = "Get File by ID", description = "Get file content by file ID")
    @ApiResponse(responseCode = "200", description = "Get file content")
    @ApiResponse(responseCode = "404", description = "File not found by ID")
    @ApiResponse(responseCode = "503", description = "File storage is unavailable")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable("fileId") Long fileId
    ) throws IOException {
        return ResponseEntity.ok()
                .header("Content-Type", "text/plain")
                .body(storageService.getFile(fileId));
    }
}