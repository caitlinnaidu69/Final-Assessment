package com.enviro.assessment.junior.caitlinnaidu.config;

import com.enviro.assessment.junior.caitlinnaidu.entity.Investor;
import com.enviro.assessment.junior.caitlinnaidu.entity.Product;
import com.enviro.assessment.junior.caitlinnaidu.entity.ProductType;
import com.enviro.assessment.junior.caitlinnaidu.repository.InvestorRepository;
import com.enviro.assessment.junior.caitlinnaidu.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Populates the in-memory H2 database with a couple of demo investors so the
 * API/UI has something to show as soon as the app starts - there's no
 * separate "register an investor" flow in this assessment's scope.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final InvestorRepository investorRepository;
    private final ProductRepository productRepository;

    public DataLoader(InvestorRepository investorRepository, ProductRepository productRepository) {
        this.investorRepository = investorRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        // Investor 1: over 65, holds a retirement annuity + a unit trust
        Investor thandi = new Investor("Thandi", "Nkosi", LocalDate.of(1955, 3, 12), "thandi.nkosi@example.com");
        investorRepository.save(thandi);
        productRepository.save(new Product(ProductType.RETIREMENT_ANNUITY, "Retirement Annuity - RA001",
                new BigDecimal("500000.00"), thandi));
        productRepository.save(new Product(ProductType.UNIT_TRUST, "Balanced Unit Trust - UT204",
                new BigDecimal("120000.00"), thandi));

        // Investor 2: under 65, so their retirement annuity withdrawals should be rejected
        Investor sipho = new Investor("Sipho", "Dlamini", LocalDate.of(1990, 7, 20), "sipho.dlamini@example.com");
        investorRepository.save(sipho);
        productRepository.save(new Product(ProductType.RETIREMENT_ANNUITY, "Retirement Annuity - RA045",
                new BigDecimal("80000.00"), sipho));
        productRepository.save(new Product(ProductType.SAVINGS_PLAN, "Flexible Savings Plan - SP112",
                new BigDecimal("25000.00"), sipho));
    }
}
