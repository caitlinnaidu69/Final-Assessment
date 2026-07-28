package com.enviro.assessment.junior.caitlinnaidu.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Payload the frontend submits when an investor requests a withdrawal.
 * Bean Validation annotations give us input validation "for free" - if a
 * request is missing a field, or the amount isn't positive, Spring rejects
 * it before it ever reaches the service layer (handled centrally by
 * GlobalExceptionHandler).
 */
public record WithdrawalRequestDto(

        @NotNull(message = "productId is required")
        Long productId,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "amount must be greater than 0")
        BigDecimal amount
) {
}
