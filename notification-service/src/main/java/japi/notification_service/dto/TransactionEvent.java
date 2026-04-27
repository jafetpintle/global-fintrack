package japi.notification_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record TransactionEvent(
    UUID transactionId,
    UUID userId,
    @Positive BigDecimal amount,
    @NotBlank String currency,
    String type // "DEPOSIT" o "WITHDRAWAL"
) {}

