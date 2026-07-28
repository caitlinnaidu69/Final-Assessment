package com.enviro.assessment.junior.caitlinnaidu.entity;

/**
 * The type of investment product an investor holds.
 * RETIREMENT_ANNUITY is special-cased by the business rules: withdrawals
 * are only permitted once the investor is over 65.
 */
public enum ProductType {
    RETIREMENT_ANNUITY,
    UNIT_TRUST,
    SAVINGS_PLAN
}
