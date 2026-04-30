package com.byteentropy.iso_20022_messaging_core.model.pacs008;

import java.time.LocalDateTime;

public record GroupHeader(
    String messageId,
    LocalDateTime creationDateTime,
    int numberOfTransactions,
    String instructingAgentBic
) {}