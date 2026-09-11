package com.poc.model;

public record DownloadedFile(
        byte[] content,
        String fileName,
        String contentType,
        long contentLength) {
}