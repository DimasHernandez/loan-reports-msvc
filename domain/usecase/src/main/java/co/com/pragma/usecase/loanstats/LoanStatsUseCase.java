package co.com.pragma.usecase.loanstats;

import co.com.pragma.model.loanstats.LoanStats;
import co.com.pragma.model.loanstats.gateways.LoanStatsRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class LoanStatsUseCase {

    private final LoanStatsRepository repository;

    public Mono<LoanStats> getLoanStats() {
        return repository.getStats();
    }

    public Mono<Void> increaseLoanStats(BigDecimal amount) {
        return repository.updateStats(amount);
    }
}
