package org.kuraterut.controller;

import org.kuraterut.dto.AnalysisResultResponse;
import org.kuraterut.dto.FileMetadataResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class FileGatewayController {

    private final WebClient fileStorageClient;
    private final WebClient fileAnalysisClient;

    public FileGatewayController(WebClient.Builder webClientBuilder) {
        this.fileStorageClient = webClientBuilder.baseUrl("http://file-storage:8081").build();
        this.fileAnalysisClient = webClientBuilder.baseUrl("http://file-analysis:8082").build();
    }

    @GetMapping("/files/{id}")
    public Mono<ResponseEntity<String>> getFileById(@PathVariable("id") Long id) {
        return fileStorageClient.get()
                .uri("/api/files/" + id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ResponseStatusException(clientResponse.statusCode(), error)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ResponseStatusException(clientResponse.statusCode(), error)))
                )
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(WebClientRequestException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("FileStorage service is unavailable: " + e.getMessage()))
                )
                .onErrorResume(ResponseStatusException.class, e ->
                        Mono.just(ResponseEntity.status(e.getStatusCode())
                                .body(e.getReason()))
                )
                .onErrorResume(Exception.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Unexpected error: " + e.getMessage()))
                );
    }

    @PostMapping(path = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity> uploadFile(@RequestPart("file") FilePart filePart) {
        return fileStorageClient.post()
                .uri("/api/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("file", filePart))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ResponseStatusException(clientResponse.statusCode(), error)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ResponseStatusException(clientResponse.statusCode(), error)))
                )
                .bodyToMono(FileMetadataResponse.class)
                .map(fileMetadata -> ResponseEntity.ok().body(fileMetadata)) // ResponseEntity<?>
                .cast(ResponseEntity.class) // <-- приводит к ResponseEntity<?>
                .onErrorResume(WebClientRequestException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body((Object) ("FileStorage service is unavailable: " + e.getMessage())))
                )
                .onErrorResume(ResponseStatusException.class, e ->
                        Mono.just(ResponseEntity.status(e.getStatusCode())
                                .body((Object) e.getReason()))
                )
                .onErrorResume(Exception.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body((Object) ("Unexpected error: " + e.getMessage())))
                );
    }


    @GetMapping("/analysis/{fileId}")
    public Mono<ResponseEntity> getAnalysisResult(@PathVariable("fileId") Long fileId) {
        return fileAnalysisClient.get()
                .uri("/api/analysis/" + fileId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ResponseStatusException(clientResponse.statusCode(), error)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ResponseStatusException(clientResponse.statusCode(), error)))
                )
                .bodyToMono(AnalysisResultResponse.class)
                .map(result -> ResponseEntity.ok().body(result))
                .cast(ResponseEntity.class)
                .onErrorResume(WebClientRequestException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body((Object) ("FileAnalysis service is unavailable: " + e.getMessage())))
                )
                .onErrorResume(ResponseStatusException.class, e ->
                        Mono.just(ResponseEntity.status(e.getStatusCode())
                                .body((Object) e.getReason()))
                )
                .onErrorResume(Exception.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body((Object) ("Unexpected error: " + e.getMessage())))
                );
    }

    @GetMapping("/analysis/wordcloud/{fileId}")
    public Mono<ResponseEntity> getWordCloudImage(@PathVariable("fileId") Long fileId) {
        return fileAnalysisClient.get()
                .uri("/api/analysis/wordcloud/" + fileId)
                .accept(MediaType.IMAGE_PNG)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ResponseStatusException(clientResponse.statusCode(), error)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new ResponseStatusException(clientResponse.statusCode(), error)))
                )
                .bodyToMono(byte[].class)
                .map(imageBytes -> ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(imageBytes))
                .cast(ResponseEntity.class)
                .onErrorResume(WebClientRequestException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body((Object) ("FileAnalysis service is unavailable: " + e.getMessage())))
                )
                .onErrorResume(ResponseStatusException.class, e ->
                        Mono.just(ResponseEntity.status(e.getStatusCode())
                                .body((Object) e.getReason()))
                )
                .onErrorResume(Exception.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body((Object) ("Unexpected error: " + e.getMessage())))
                );
    }


}
