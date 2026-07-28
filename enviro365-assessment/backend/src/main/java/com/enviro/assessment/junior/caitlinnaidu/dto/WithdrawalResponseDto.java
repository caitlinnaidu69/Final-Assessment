package com.enviro.assessment.junior.caitlinnaidu.dto;

import com.enviro.assessment.junior.caitlinnaidu.entity.WithdrawalNotice;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public record WithdrawalResponseDto(
        Long id,
        Long productId,
        String productName,
        BigDecimal amount,
        BigDecimal balanceAtRequest,
        String status,
        String reason,
        String requestedAt
) {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static WithdrawalResponseDto fromEntity(WithdrawalNotice notice) {
        return new WithdrawalResponseDto(
                notice.getId(),
                notice.getProduct().getId(),
                notice.getProduct().getProductName(),
                notice.getAmount(),
                notice.getBalanceAtRequest(),
                notice.getStatus().name(),
                notice.getReason(),
                notice.getRequestedAt().format(FORMAT)
        );
    }
}
