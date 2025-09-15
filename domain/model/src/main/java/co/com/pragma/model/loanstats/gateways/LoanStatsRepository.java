package co.com.pragma.model.loanstats.gateways;

import co.com.pragma.model.loanstats.LoanStats;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface LoanStatsRepository {

    Mono<LoanStats> getStats();

    Mono<Void> updateStats(BigDecimal amountToAdd);
}
