package com.enviro.assessment.junior.caitlinnaidu.service;

import com.enviro.assessment.junior.caitlinnaidu.entity.WithdrawalNotice;
import com.enviro.assessment.junior.caitlinnaidu.entity.WithdrawalStatus;
import com.enviro.assessment.junior.caitlinnaidu.repository.WithdrawalNoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Builds the CSV withdrawal statement for an investor.
 * Filtering is optional: status, and/or a from/to date range on requestedAt.
 * Kept deliberately simple (in-memory filtering over a small H2 dataset) -
 * for a production system with large volumes this filtering would move into
 * the repository query instead.
 */
@Service
public class ReportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final WithdrawalNoticeRepository withdrawalNoticeRepository;

    public ReportService(WithdrawalNoticeRepository withdrawalNoticeRepository) {
        this.withdrawalNoticeRepository = withdrawalNoticeRepository;
    }

    @Transactional(readOnly = true)
    public String buildWithdrawalsCsv(Long investorId, WithdrawalStatus statusFilter,
                                       LocalDate fromDate, LocalDate toDate) {
        List<WithdrawalNotice> notices = withdrawalNoticeRepository
                .findByProduct_Investor_IdOrderByRequestedAtDesc(investorId);

        StringBuilder csv = new StringBuilder();
        csv.append("Notice ID,Product Name,Amount,Balance Before Withdrawal,Status,Reason,Requested At\n");

        for (WithdrawalNotice notice : notices) {
            if (statusFilter != null && notice.getStatus() != statusFilter) {
                continue;
            }
            LocalDate requestedDate = notice.getRequestedAt().toLocalDate();
            if (fromDate != null && requestedDate.isBefore(fromDate)) {
                continue;
            }
            if (toDate != null && requestedDate.isAfter(toDate)) {
                continue;
            }

            csv.append(notice.getId()).append(",")
                    .append(escapeCsv(notice.getProduct().getProductName())).append(",")
                    .append(notice.getAmount()).append(",")
                    .append(notice.getBalanceAtRequest()).append(",")
                    .append(notice.getStatus()).append(",")
                    .append(escapeCsv(notice.getReason() == null ? "" : notice.getReason())).append(",")
                    .append(notice.getRequestedAt().format(DATE_FORMAT))
                    .append("\n");
        }

        return csv.toString();
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
