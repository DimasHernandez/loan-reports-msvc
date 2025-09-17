package co.com.pragma.usecase.loanapproved;

import co.com.pragma.model.loanreportevent.LoanReportEvent;
import co.com.pragma.model.loanreportevent.enums.STATES;
import co.com.pragma.model.loanreportevent.gateways.LoanReportEventRepository;
import co.com.pragma.model.loanreportevent.gateways.LoggerGateway;
import co.com.pragma.usecase.loanstats.LoanStatsUseCase;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class LoanApprovedUseCase {

    private final LoanReportEventRepository loanReportEventRepository;
    private final LoanStatsUseCase loanStatsUseCase;
    private final LoggerGateway logger;

    public Mono<Void> processMessageApprovedLoanApplicationsReportQueue(LoanReportEvent loanReportEvent) {
        UUID loanId = loanReportEvent.getLoanId();
        String status = loanReportEvent.getStatus();
        BigDecimal amount = loanReportEvent.getAmount();

        if (shouldSkipProcessingMessage(loanId, status, amount)) {
            return Mono.empty();
        }

        return loanReportEventRepository.save(loanReportEvent)
                .doOnSuccess(saved -> logger.info("Loan report saved successfully with loan_id: {}", saved.getLoanId()))
                .doOnError(e -> logger.error("Failed to save LoanReportEvent. Error: {}", e.getMessage()))
                .flatMap(saved -> loanStatsUseCase.increaseLoanStats(saved.getAmount())
                        .doOnSuccess(v -> logger.info("Loan stats updated successfully for amount: {}", saved.getAmount()))
                        .doOnError(e -> logger.error("Failed to update loan stats. Error: {}", e.getMessage()))
                )
                .onErrorResume(e -> {
                    logger.error("An error occurred during processing: {}", e.getMessage());
                    return Mono.empty();
                })
                .then();
    }

    private boolean shouldSkipProcessingMessage(UUID loanId, String status, BigDecimal amount) {
        if (loanId == null || status == null || status.trim().isEmpty() || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Skipping message due to invalid fields - loanId: {}, status: '{}', amount: {}", loanId, status, amount);
            return true;

        }
        if (!STATES.APPROVED.name().equals(status.toUpperCase())) {
            logger.warn("The status is not valid. Status: {}", status);
            return true;
        }
        return false;
    }
}
