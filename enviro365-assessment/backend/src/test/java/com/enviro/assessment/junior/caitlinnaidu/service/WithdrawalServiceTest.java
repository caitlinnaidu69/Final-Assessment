package com.enviro.assessment.junior.caitlinnaidu.service;

import com.enviro.assessment.junior.caitlinnaidu.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.caitlinnaidu.dto.WithdrawalResponseDto;
import com.enviro.assessment.junior.caitlinnaidu.entity.Investor;
import com.enviro.assessment.junior.caitlinnaidu.entity.Product;
import com.enviro.assessment.junior.caitlinnaidu.entity.ProductType;
import com.enviro.assessment.junior.caitlinnaidu.exception.BusinessRuleException;
import com.enviro.assessment.junior.caitlinnaidu.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.caitlinnaidu.repository.ProductRepository;
import com.enviro.assessment.junior.caitlinnaidu.repository.WithdrawalNoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the business rules in WithdrawalService.
 * These use Mockito to fake the repositories, so the tests run against the
 * rule logic only, not a real database - fast, and each test isolates one
 * rule at a time.
 */
class WithdrawalServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WithdrawalNoticeRepository withdrawalNoticeRepository;

    private WithdrawalService withdrawalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        withdrawalService = new WithdrawalService(productRepository, withdrawalNoticeRepository);
    }

    private Investor investorAged(int age) {
        return new Investor("Test", "Investor", LocalDate.now().minusYears(age), "test@example.com");
    }

    @Test
    void retirementWithdrawal_rejectedWhenInvestorIsNotOver65() {
        Investor investor = investorAged(60);
        Product product = new Product(ProductType.RETIREMENT_ANNUITY, "RA Test", new BigDecimal("100000"), investor);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        WithdrawalRequestDto request = new WithdrawalRequestDto(1L, new BigDecimal("1000"));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> withdrawalService.createWithdrawal(request));
        assertTrue(ex.getMessage().contains("over the age of 65"));
    }

    @Test
    void retirementWithdrawal_allowedWhenInvestorIsOver65() {
        Investor investor = investorAged(70);
        Product product = new Product(ProductType.RETIREMENT_ANNUITY, "RA Test", new BigDecimal("100000"), investor);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(withdrawalNoticeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawalRequestDto request = new WithdrawalRequestDto(1L, new BigDecimal("1000"));

        WithdrawalResponseDto response = withdrawalService.createWithdrawal(request);
        assertEquals("APPROVED", response.status());
    }

    @Test
    void withdrawal_rejectedWhenAmountExceedsBalance() {
        Investor investor = investorAged(40);
        Product product = new Product(ProductType.UNIT_TRUST, "UT Test", new BigDecimal("1000"), investor);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        WithdrawalRequestDto request = new WithdrawalRequestDto(1L, new BigDecimal("2000"));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> withdrawalService.createWithdrawal(request));
        assertTrue(ex.getMessage().contains("exceeds available balance"));
    }

    @Test
    void withdrawal_rejectedWhenAmountExceeds90PercentOfBalance() {
        Investor investor = investorAged(40);
        Product product = new Product(ProductType.UNIT_TRUST, "UT Test", new BigDecimal("1000"), investor);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // 950 is within balance but above the 90% cap of 900
        WithdrawalRequestDto request = new WithdrawalRequestDto(1L, new BigDecimal("950"));

        BusinessRuleException ex = assertThrows(BusinessRuleException.class,
                () -> withdrawalService.createWithdrawal(request));
        assertTrue(ex.getMessage().contains("90%"));
    }

    @Test
    void withdrawal_approvedWhenWithinLimits() {
        Investor investor = investorAged(40);
        Product product = new Product(ProductType.UNIT_TRUST, "UT Test", new BigDecimal("1000"), investor);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(withdrawalNoticeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        WithdrawalRequestDto request = new WithdrawalRequestDto(1L, new BigDecimal("500"));

        WithdrawalResponseDto response = withdrawalService.createWithdrawal(request);
        assertEquals("APPROVED", response.status());
        assertEquals(new BigDecimal("500"), product.getBalance());
    }

    @Test
    void withdrawal_throwsWhenProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        WithdrawalRequestDto request = new WithdrawalRequestDto(99L, new BigDecimal("100"));

        assertThrows(ResourceNotFoundException.class, () -> withdrawalService.createWithdrawal(request));
    }
}
