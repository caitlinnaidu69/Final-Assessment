package com.enviro.assessment.junior.caitlinnaidu.repository;

import com.enviro.assessment.junior.caitlinnaidu.entity.WithdrawalNotice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalNoticeRepository extends JpaRepository<WithdrawalNotice, Long> {

    // Used by the history endpoint and the CSV export - joins through product -> investor.
    List<WithdrawalNotice> findByProduct_Investor_IdOrderByRequestedAtDesc(Long investorId);
}
