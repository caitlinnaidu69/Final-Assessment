package com.enviro.assessment.junior.caitlinnaidu.service;

import com.enviro.assessment.junior.caitlinnaidu.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.caitlinnaidu.dto.WithdrawalResponseDto;
import com.enviro.assessment.junior.caitlinnaidu.entity.Product;
import com.enviro.assessment.junior.caitlinnaidu.entity.ProductType;
import com.enviro.assessment.junior.caitlinnaidu.entity.WithdrawalNotice;
import com.enviro.assessment.junior.caitlinnaidu.entity.WithdrawalStatus;
import com.enviro.assessment.junior.caitlinnaidu.exception.BusinessRuleException;
import com.enviro.assessment.junior.caitlinnaidu.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.caitlinnaidu.repository.ProductRepository;
import com.enviro.assessment.junior.caitlinnaidu.repository.WithdrawalNoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Encapsulates every withdrawal-related business rule in one place, so the
 * controller stays a thin HTTP layer and the rules are easy to unit test in
 * isolation (see WithdrawalServiceTest).
 *
 * Rules enforced, in this order:
 *   1. The product must exist.
 *   2. Retirement annuity withdrawals are only allowed if the investor is over 65.
 *   3. The withdrawal amount may not exceed the available balance.
 *   4. The withdrawal amount may not exceed 90% of the available balance
 *      (a stricter cap that applies to every product type).
 *
 * A rule violation throws BusinessRuleException, which GlobalExceptionHandler
 * turns into a 400 response with a clear message - nothing is persisted for
 * a rejected request, keeping the withdrawal_notices table as a clean record
 * of successful withdrawals only.
 */
@Service
public class WithdrawalService {

    private static final BigDecimal MAX_WITHDRAWAL_PERCENTAGE = new BigDecimal("0.90");
    private static final int RETIREMENT_MINIMUM_AGE = 65;

    private final ProductRepository productRepository;
    private final WithdrawalNoticeRepository withdrawalNoticeRepository;

    public WithdrawalService(ProductRepository productRepository,
                              WithdrawalNoticeRepository withdrawalNoticeRepository) {
        this.productRepository = productRepository;
        this.withdrawalNoticeRepository = withdrawalNoticeRepository;
    }

    @Transactional
    public WithdrawalResponseDto createWithdrawal(WithdrawalRequestDto request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.productId()));

        validateBusinessRules(product, request.amount());

        // All rules passed - debit the balance and record the notice.
        BigDecimal balanceBeforeWithdrawal = product.getBalance();
        product.setBalance(balanceBeforeWithdrawal.subtract(request.amount()));
        productRepository.save(product);

        WithdrawalNotice notice = new WithdrawalNotice(
                product,
                request.amount(),
                balanceBeforeWithdrawal,
                WithdrawalStatus.APPROVED,
                null,
                LocalDateTime.now()
        );
        withdrawalNoticeRepository.save(notice);

        return WithdrawalResponseDto.fromEntity(notice);
    }

    private void validateBusinessRules(Product product, BigDecimal amount) {
        // Rule 1: Retirement withdrawals only allowed if age > 65
        if (product.getProductType() == ProductType.RETIREMENT_ANNUITY) {
            int investorAge = product.getInvestor().getAge();
            if (investorAge <= RETIREMENT_MINIMUM_AGE) {
                throw new BusinessRuleException(
                        "Retirement annuity withdrawals are only allowed for investors over the age of "
                                + RETIREMENT_MINIMUM_AGE + ". Investor is currently " + investorAge + ".");
            }
        }

        // Rule 2: Withdrawal must not exceed balance
        if (amount.compareTo(product.getBalance()) > 0) {
            throw new BusinessRuleException(
                    "Withdrawal amount (" + amount + ") exceeds available balance (" + product.getBalance() + ").");
        }

        // Rule 3: Withdrawal must not exceed 90% of balance
        BigDecimal maxAllowed = product.getBalance().multiply(MAX_WITHDRAWAL_PERCENTAGE);
        if (amount.compareTo(maxAllowed) > 0) {
            throw new BusinessRuleException(
                    "Withdrawal amount (" + amount + ") exceeds the maximum allowed withdrawal of 90% of balance ("
                            + maxAllowed + ").");
        }
    }

    @Transactional(readOnly = true)
    public List<WithdrawalResponseDto> getHistoryForInvestor(Long investorId) {
        return withdrawalNoticeRepository.findByProduct_Investor_IdOrderByRequestedAtDesc(investorId)
                .stream()
                .map(WithdrawalResponseDto::fromEntity)
                .toList();
    }
}
