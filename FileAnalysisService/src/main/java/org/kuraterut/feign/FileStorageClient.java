package org.kuraterut.feign;

import lombok.extern.slf4j.Slf4j;
import org.kuraterut.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "file-storage",
        url = "${file-storage.service.url}",
        configuration = FeignConfig.class,
        fallback = FileStorageClient.Fallback.class
)
public interface FileStorageClient {

    @GetMapping("/api/files/{fileId}")
    ResponseEntity<byte[]> getFile(@PathVariable("fileId") Long fileId);

    @Component
    @Slf4j
    class Fallback implements FileStorageClient {
        @Override
        public ResponseEntity<byte[]> getFile(Long fileId) {
            log.warn("Using fallback for FileStorageClient, fileId: {}", fileId);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("File Storage Service is unavailable".getBytes());
        }
    }
}

