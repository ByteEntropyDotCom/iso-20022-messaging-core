package com.byteentropy.iso_20022_messaging_core.model.pacs008;

import java.math.BigDecimal;

public record CreditTransferTransaction(
    String endToEndId,
    BigDecimal amount,
    String currency,
    String debtorName,
    String debtorIban,
    String creditorName,
    String creditorIban
) {}