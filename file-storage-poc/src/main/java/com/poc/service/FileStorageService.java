package com.poc.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.poc.dto.FileMetadataResponse;
import com.poc.dto.FileUploadResponse;
import com.poc.exception.FileStorageException;
import com.poc.exception.InvalidFileException;
import com.poc.exception.ObjectNotFoundException;
import com.poc.model.DownloadedFile;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
public class FileStorageService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "text/plain");

    private final S3Client s3Client;
    private final String bucketName;
    private final String folder;

    public FileStorageService(
            S3Client s3Client,
            @Value("${app.aws.s3.bucket-name}") String bucketName,
            @Value("${app.aws.s3.folder}") String folder) {

        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.folder = folder;
    }

    public FileUploadResponse upload(MultipartFile file) {

        validateFile(file);

        String originalFileName =
                StringUtils.cleanPath(file.getOriginalFilename());

        String objectKey = buildObjectKey(originalFileName);

        String contentType = file.getContentType() == null
                ? "application/octet-stream"
                : file.getContentType();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();

        try {
            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()));

            return new FileUploadResponse(
                    "File uploaded successfully",
                    objectKey,
                    originalFileName,
                    contentType,
                    file.getSize());

        } catch (IOException exception) {
            throw new FileStorageException(
                    "Unable to read the uploaded file",
                    exception);

        } catch (S3Exception exception) {
            throw new FileStorageException(
                    awsErrorMessage(
                            "Unable to upload file to Amazon S3",
                            exception),
                    exception);
        }
    }

    public DownloadedFile download(String objectKey) {

        validateObjectKey(objectKey);

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        try {
            ResponseBytes<GetObjectResponse> response =
                    s3Client.getObjectAsBytes(request);

            GetObjectResponse metadata = response.response();

            String contentType = metadata.contentType() == null
                    ? "application/octet-stream"
                    : metadata.contentType();

            return new DownloadedFile(
                    response.asByteArray(),
                    extractFileName(objectKey),
                    contentType,
                    metadata.contentLength());

        } catch (NoSuchKeyException exception) {
            throw new ObjectNotFoundException(
                    "File not found for object key: " + objectKey);

        } catch (S3Exception exception) {

            if (exception.statusCode() == 404) {
                throw new ObjectNotFoundException(
                        "File not found for object key: "
                                + objectKey);
            }

            throw new FileStorageException(
                    awsErrorMessage(
                            "Unable to download file from Amazon S3",
                            exception),
                    exception);
        }
    }

    public void delete(String objectKey) {

        validateObjectKey(objectKey);

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        try {
            s3Client.deleteObject(request);

        } catch (S3Exception exception) {
            throw new FileStorageException(
                    awsErrorMessage(
                            "Unable to delete file from Amazon S3",
                            exception),
                    exception);
        }
    }

    public List<FileMetadataResponse> list() {

        String prefix = normalizedFolderPrefix();

        ListObjectsV2Request request =
                ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .build();

        try {
            return s3Client.listObjectsV2Paginator(request)
                    .contents()
                    .stream()
                    .map(object -> new FileMetadataResponse(
                            object.key(),
                            object.size(),
                            object.lastModified(),
                            object.eTag()))
                    .toList();

        } catch (S3Exception exception) {
            throw new FileStorageException(
                    awsErrorMessage(
                            "Unable to list files from Amazon S3",
                            exception),
                    exception);
        }
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileException(
                    "Please select a non-empty file");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null
                || originalFileName.isBlank()) {

            throw new InvalidFileException(
                    "File name is required");
        }

        String cleanFileName =
                StringUtils.cleanPath(originalFileName);

        if (cleanFileName.contains("..")) {
            throw new InvalidFileException(
                    "File name contains an invalid path sequence");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                    "File size must not exceed 10 MB");
        }

        String contentType = file.getContentType();

        if (contentType == null
                || !ALLOWED_CONTENT_TYPES.contains(contentType)) {

            throw new InvalidFileException(
                    "Only PDF, PNG, JPEG and plain text files are allowed");
        }
    }

    private void validateObjectKey(String objectKey) {

        if (objectKey == null || objectKey.isBlank()) {
            throw new InvalidFileException(
                    "Object key must not be empty");
        }

        if (objectKey.contains("..")) {
            throw new InvalidFileException(
                    "Object key contains an invalid path sequence");
        }
    }

    private String buildObjectKey(String originalFileName) {

        return normalizedFolderPrefix()
                + UUID.randomUUID()
                + "-"
                + originalFileName.replace(" ", "_");
    }

    private String normalizedFolderPrefix() {

        if (folder == null || folder.isBlank()) {
            return "";
        }

        return folder.endsWith("/")
                ? folder
                : folder + "/";
    }

    private String extractFileName(String objectKey) {

        int lastSlash = objectKey.lastIndexOf('/');

        return lastSlash >= 0
                ? objectKey.substring(lastSlash + 1)
                : objectKey;
    }

    private String awsErrorMessage(
            String defaultMessage,
            S3Exception exception) {

        if (exception.awsErrorDetails() != null
                && exception.awsErrorDetails()
                        .errorMessage() != null) {

            return defaultMessage
                    + ": "
                    + exception.awsErrorDetails()
                            .errorMessage();
        }

        return defaultMessage;
    }
}