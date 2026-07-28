package com.enviro.assessment.junior.caitlinnaidu.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawal_notices")
public class WithdrawalNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private BigDecimal amount;

    /** Balance snapshot at the time of the request — kept so history/statements stay accurate
     *  even if the product balance changes later. */
    private BigDecimal balanceAtRequest;

    @Enumerated(EnumType.STRING)
    private WithdrawalStatus status;

    /** Human-readable reason, populated mainly when status = REJECTED. */
    private String reason;

    private LocalDateTime requestedAt;

    protected WithdrawalNotice() {
        // JPA
    }

    public WithdrawalNotice(Product product, BigDecimal amount, BigDecimal balanceAtRequest,
                             WithdrawalStatus status, String reason, LocalDateTime requestedAt) {
        this.product = product;
        this.amount = amount;
        this.balanceAtRequest = balanceAtRequest;
        this.status = status;
        this.reason = reason;
        this.requestedAt = requestedAt;
    }

    // --- getters / setters ---
    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getBalanceAtRequest() { return balanceAtRequest; }
    public void setBalanceAtRequest(BigDecimal balanceAtRequest) { this.balanceAtRequest = balanceAtRequest; }
    public WithdrawalStatus getStatus() { return status; }
    public void setStatus(WithdrawalStatus status) { this.status = status; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }
}
