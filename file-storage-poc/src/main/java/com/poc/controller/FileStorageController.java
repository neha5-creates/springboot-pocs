package com.poc.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poc.dto.FileMetadataResponse;
import com.poc.dto.FileUploadResponse;
import com.poc.model.DownloadedFile;
import com.poc.service.FileStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/files")
@Tag(
        name = "File Storage API",
        description = "Upload, download, list and delete files in Amazon S3")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    public FileStorageController(
            FileStorageService fileStorageService) {

        this.fileStorageService = fileStorageService;
    }

    @Operation(summary = "Upload a file to Amazon S3")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(
            @RequestPart("file") MultipartFile file) {

        FileUploadResponse response =
                fileStorageService.upload(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "Download a file from Amazon S3")
    @GetMapping("/download")
public ResponseEntity<ByteArrayResource>
download(
      @RequestParam String objectKey) {

    DownloadedFile downloadedFile =
            fileStorageService.download(objectKey);

    ByteArrayResource resource =
            new ByteArrayResource(
                    downloadedFile.content());

    return ResponseEntity.ok()
            .header(
              HttpHeaders.CONTENT_DISPOSITION,
              "attachment; filename=\""
                      + downloadedFile.fileName()
                      + "\"")
            .contentType(
               MediaType.parseMediaType(
                  downloadedFile.contentType()))
            .contentLength(
                  downloadedFile.contentLength())
            .body(resource);
}


    @Operation(summary = "List files stored in Amazon S3")
    @GetMapping
    public ResponseEntity<List<FileMetadataResponse>> list() {

        return ResponseEntity.ok(
                fileStorageService.list());
    }

    @Operation(summary = "Delete a file from Amazon S3")
   @DeleteMapping
public ResponseEntity<Void> delete(
@RequestParam String objectKey) {
fileStorageService.delete(objectKey);
return ResponseEntity.noContent().build();
}

    private MediaType parseMediaType(String contentType) {

        try {
            return MediaType.parseMediaType(contentType);
        } catch (Exception exception) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

//     @GetMapping("/download")
//     public ResponseEntity<ByteArrayResource> download(
//     @RequestParam String objectKey) {
// // same implementation
// }
}