package com.enviro.assessment.junior.caitlinnaidu.dto;

import com.enviro.assessment.junior.caitlinnaidu.entity.Product;

import java.math.BigDecimal;

/**
 * Read-only view of a Product returned to the frontend.
 * We never expose the JPA entity directly, so the API contract doesn't
 * accidentally change just because the database model changes.
 */
public record ProductDto(
        Long id,
        String productType,
        String productName,
        BigDecimal balance
) {
    public static ProductDto fromEntity(Product product) {
        return new ProductDto(
                product.getId(),
                product.getProductType().name(),
                product.getProductName(),
                product.getBalance()
        );
    }
}
