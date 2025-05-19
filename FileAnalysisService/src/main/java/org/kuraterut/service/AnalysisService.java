package org.kuraterut.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kuraterut.exception.FeignException;
import org.kuraterut.exception.FileNotFoundException;
import org.kuraterut.feign.FileStorageClient;
import org.kuraterut.model.AnalysisResult;
import org.kuraterut.repository.AnalysisResultRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {
    private final AnalysisResultRepository repository;
    private final FileStorageClient storageClient;
    private final WordCloudService wordCloudService;

    @Value("${wordcloud.output.dir}")
    private String wordCloudStoragePath;

    @CircuitBreaker(name = "fileStorage", fallbackMethod = "analyzeFileFallback")
    @Retry(name = "fileStorage")
    public AnalysisResult analyzeFile(Long fileId) throws IOException {
        ResponseEntity<byte[]> fileContent = storageClient.getFile(fileId);
        if (fileContent.getStatusCode() == HttpStatusCode.valueOf(503)) {
            throw new FeignException("Service is unavailable");
        }
        else if (fileContent.getStatusCode().is4xxClientError()){
            throw new org.kuraterut.exception.FileNotFoundException("File not found with id " + fileId);
        }
        assert fileContent != null;
        String text = new String(fileContent.getBody());

        int charCount = text.length();
        int wordCount = text.split("\\s+").length;
        int paragraphCount = text.split("\n\n").length;

        byte[] wordCloud = wordCloudService.generateWordCloud(text);

        String filename = "wordcloud_" + fileId + ".png";
        Path outputPath = saveWordCloudImage(wordCloud, filename);

        AnalysisResult result = new AnalysisResult();
        result.setFileId(fileId);
        result.setCharCount(charCount);
        result.setWordCount(wordCount);
        result.setParagraphCount(paragraphCount);
        result.setWordCloudUri(outputPath.toString());

        return repository.save(result);
    }

    public AnalysisResult analyzeFileFallback(Long fileId, Exception e) {
        log.warn("Using fallback for file analysis, fileId: {}", fileId, e);
        if (e instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) e;
            if(responseStatusException.getStatusCode().is4xxClientError()){
                throw new org.kuraterut.exception.FileNotFoundException("File not found with id " + fileId);
            }
            else if (responseStatusException.getStatusCode().is5xxServerError()){
                throw new FeignException(e.getMessage());
            }
            throw new RuntimeException("File storage exception", e);
        }
        throw new FeignException(e.getMessage());
    }

    public byte[] getWordCloudImage(Long fileId) throws IOException {
        String filename = "wordcloud_" + fileId + ".png";
        Path filePath = Paths.get(wordCloudStoragePath).resolve(filename);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Word cloud image not found: " + filename);
        }

        return Files.readAllBytes(filePath);
    }

    private Path saveWordCloudImage(byte[] imageBytes, String filename) throws IOException {
        Path storagePath = Paths.get(wordCloudStoragePath);
        if (!Files.exists(storagePath)) {
            Files.createDirectories(storagePath);
        }

        Path outputPath = storagePath.resolve(filename);
        Files.write(outputPath, imageBytes);

        return outputPath;
    }
}