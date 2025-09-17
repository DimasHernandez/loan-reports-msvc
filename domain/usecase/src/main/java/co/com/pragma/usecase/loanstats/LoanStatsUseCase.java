package co.com.pragma.usecase.loanstats;

import co.com.pragma.model.loanreportevent.gateways.LoggerGateway;
import co.com.pragma.model.loanstats.LoanStats;
import co.com.pragma.model.loanstats.gateways.LoanStatsRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class LoanStatsUseCase {

    private final LoanStatsRepository loanStatsRepository;
    private final LoggerGateway logger;

    public Mono<LoanStats> getLoanStats() {
        return loanStatsRepository.getStats()
                .doOnSuccess(loanStats -> logger.info("Checking report metrics - total_approved_loans: " +
                        "{}, total_approved_amount: {}", loanStats.getTotalApprovedLoans(), loanStats.getTotalApprovedAmount()))
                .doOnError(e -> logger.error("An error occurred while retrieving report metrics. Message: {}", e.getMessage()));
    }

    public Mono<Void> increaseLoanStats(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.warn("Cannot update record in database amount: {}", amount);
            return Mono.empty();
        }

        return loanStatsRepository.updateStats(amount)
                .doOnSuccess(v -> logger.info("Successfully updating report metrics."))
                .doOnError(e -> logger.error("An error occurred while updating report metrics. Message: {}", e.getMessage()));
    }
}
