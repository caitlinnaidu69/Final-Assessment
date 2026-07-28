package com.enviro.assessment.junior.caitlinnaidu.service;

import com.enviro.assessment.junior.caitlinnaidu.dto.PortfolioDto;
import com.enviro.assessment.junior.caitlinnaidu.entity.Investor;
import com.enviro.assessment.junior.caitlinnaidu.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.caitlinnaidu.repository.InvestorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PortfolioService {

    private final InvestorRepository investorRepository;

    public PortfolioService(InvestorRepository investorRepository) {
        this.investorRepository = investorRepository;
    }

    @Transactional(readOnly = true)
    public PortfolioDto getPortfolio(Long investorId) {
        Investor investor = investorRepository.findById(investorId)
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found with id: " + investorId));
        return PortfolioDto.fromEntity(investor);
    }
}
