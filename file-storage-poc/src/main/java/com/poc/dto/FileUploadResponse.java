package com.poc.dto;

public record FileUploadResponse(
        String message,
        String objectKey,
        String originalFileName,
        String contentType,
        long size) {
}