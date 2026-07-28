package com.enviro.assessment.junior.caitlinnaidu.exception;

/**
 * Thrown when a request is well-formed (passes bean validation) but breaks
 * one of the domain business rules, e.g. withdrawal exceeds balance,
 * retirement withdrawal requested before age 65, etc.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
