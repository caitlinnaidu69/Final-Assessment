package com.enviro.assessment.junior.caitlinnaidu.controller;

import com.enviro.assessment.junior.caitlinnaidu.entity.WithdrawalStatus;
import com.enviro.assessment.junior.caitlinnaidu.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // GET /api/reports/investor/1/withdrawals.csv?status=APPROVED&from=2026-01-01&to=2026-12-31
    // All query params are optional filters.
    @GetMapping("/investor/{investorId}/withdrawals.csv")
    public ResponseEntity<String> exportWithdrawalsCsv(
            @PathVariable Long investorId,
            @RequestParam(required = false) WithdrawalStatus status,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to
    ) {
        String csv = reportService.buildWithdrawalsCsv(investorId, status, from, to);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"withdrawal-statement-investor-" + investorId + ".csv\"")
                .body(csv);
    }
}
