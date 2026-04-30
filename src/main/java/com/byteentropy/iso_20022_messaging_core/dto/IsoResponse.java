package com.byteentropy.iso_20022_messaging_core.dto;

import java.time.LocalDateTime;

public record IsoResponse(
    String messageId,
    String type, // e.g., "pacs.008.001.10"
    LocalDateTime generatedAt,
    String rawXml
) {}
