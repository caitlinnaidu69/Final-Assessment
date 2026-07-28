package com.enviro.assessment.junior.caitlinnaidu.controller;

import com.enviro.assessment.junior.caitlinnaidu.dto.WithdrawalRequestDto;
import com.enviro.assessment.junior.caitlinnaidu.dto.WithdrawalResponseDto;
import com.enviro.assessment.junior.caitlinnaidu.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    // POST /api/withdrawals
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WithdrawalResponseDto createWithdrawal(@Valid @RequestBody WithdrawalRequestDto request) {
        return withdrawalService.createWithdrawal(request);
    }

    // GET /api/withdrawals/investor/1
    @GetMapping("/investor/{investorId}")
    public List<WithdrawalResponseDto> getHistory(@PathVariable Long investorId) {
        return withdrawalService.getHistoryForInvestor(investorId);
    }
}
