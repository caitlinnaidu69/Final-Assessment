package com.enviro.assessment.junior.caitlinnaidu.controller;

import com.enviro.assessment.junior.caitlinnaidu.dto.PortfolioDto;
import com.enviro.assessment.junior.caitlinnaidu.service.PortfolioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/investors")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // GET /api/investors/1/portfolio
    @GetMapping("/{investorId}/portfolio")
    public PortfolioDto getPortfolio(@PathVariable Long investorId) {
        return portfolioService.getPortfolio(investorId);
    }
}
