package com.byteentropy.iso_20022_messaging_core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record InternalPaymentRequest(
    @NotBlank(message = "Transaction reference is required")
    String transactionRef,

    @NotNull(message = "Amount is required")
    @Positive(message = "Payment amount must be greater than zero")
    BigDecimal amount,

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO 4217 code")
    String currency,

    @NotBlank(message = "Sender name is required")
    String senderName,

    @NotBlank(message = "Sender IBAN is required")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$", message = "Invalid Sender IBAN format")
    String senderIban,

    @NotBlank(message = "Receiver name is required")
    String receiverName,

    @NotBlank(message = "Receiver IBAN is required")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$", message = "Invalid Receiver IBAN format")
    String receiverIban
) {}