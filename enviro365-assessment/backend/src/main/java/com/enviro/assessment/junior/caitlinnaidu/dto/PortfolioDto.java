package com.enviro.assessment.junior.caitlinnaidu.dto;

import com.enviro.assessment.junior.caitlinnaidu.entity.Investor;

import java.util.List;

public record PortfolioDto(
        Long investorId,
        String fullName,
        int age,
        String email,
        List<ProductDto> products
) {
    public static PortfolioDto fromEntity(Investor investor) {
        List<ProductDto> products = investor.getProducts().stream()
                .map(ProductDto::fromEntity)
                .toList();

        return new PortfolioDto(
                investor.getId(),
                investor.getFirstName() + " " + investor.getLastName(),
                investor.getAge(),
                investor.getEmail(),
                products
        );
    }
}
