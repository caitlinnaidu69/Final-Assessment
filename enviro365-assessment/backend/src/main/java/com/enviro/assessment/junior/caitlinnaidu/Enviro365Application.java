package com.enviro.assessment.junior.caitlinnaidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Enviro365 Investments withdrawal-notice system.
 *
 * Scenario: investors can view their portfolio (investor details + the
 * products/policies they hold), submit withdrawal notices against a
 * product, have those notices validated against business rules, and
 * export their withdrawal history as a CSV statement.
 */
@SpringBootApplication
public class Enviro365Application {

    public static void main(String[] args) {
        SpringApplication.run(Enviro365Application.class, args);
    }
}
