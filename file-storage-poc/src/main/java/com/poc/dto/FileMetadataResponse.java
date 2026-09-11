package com.poc.dto;

import java.time.Instant;

public record FileMetadataResponse(
        String objectKey,
        long size,
        Instant lastModified,
        String eTag) {
}