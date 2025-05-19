package org.kuraterut.controller;

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
public class FileStorageController {
    private final FileStorageService storageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileMetadata> uploadFile(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        log.info("Content-Type: {}", file.getContentType());
        log.info("File received: {} ({} bytes)", file.getOriginalFilename(), file.getSize());
        return ResponseEntity.ok(storageService.saveFile(file));
    }


    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable("fileId") Long fileId
    ) throws IOException {
        return ResponseEntity.ok()
                .header("Content-Type", "text/plain")
                .body(storageService.getFile(fileId));
    }
}