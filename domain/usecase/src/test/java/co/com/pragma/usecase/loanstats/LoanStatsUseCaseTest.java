package co.com.pragma.usecase.loanstats;

import co.com.pragma.model.loanreportevent.gateways.LoggerGateway;
import co.com.pragma.model.loanstats.LoanStats;
import co.com.pragma.model.loanstats.gateways.LoanStatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanStatsUseCaseTest {

    @Mock
    private LoanStatsRepository loanStatsRepository;

    @Mock
    private LoggerGateway logger;

    @InjectMocks
    private LoanStatsUseCase loanStatsUseCase;

    @Test
    void shouldReturnLoanStatsSuccessfully() {
        // Arrange
        LoanStats loanStats = loanStatsMock();

        // when reactive repositories
        when(loanStatsRepository.getStats()).thenReturn(Mono.just(loanStats));

        // Act
        Mono<LoanStats> result = loanStatsUseCase.getLoanStats();

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(loanStatsReport -> loanStatsReport.getTotalApprovedLoans() == 5L &&
                        loanStatsReport.getTotalApprovedAmount().compareTo(new BigDecimal("11500000.00")) >= 0
                )
                .verifyComplete();
    }

    private LoanStats loanStatsMock() {
        return LoanStats.builder()
                .totalApprovedLoans(5L)
                .totalApprovedAmount(new BigDecimal("11500000.00"))
                .build();
    }

    @Test
    void shouldIncreaseLoanStatsSuccessfully() {
        // Arrange
        BigDecimal amount = new BigDecimal("300000.00");

        // when reactive repositories
        when(loanStatsRepository.updateStats(any(BigDecimal.class))).thenReturn(Mono.empty());

        // Assert
        StepVerifier.create(loanStatsUseCase.increaseLoanStats(amount))
                .verifyComplete();

        verify(loanStatsRepository, times(1)).updateStats(any(BigDecimal.class));
    }

    @ParameterizedTest
    @MethodSource("amountValues")
    void shouldIncreaseLoanStatsFailsAmountNullOrLessThenZero(BigDecimal amount) {
        // Arrange

        StepVerifier.create(loanStatsUseCase.increaseLoanStats(amount))
                .verifyComplete();

        verify(loanStatsRepository, never()).updateStats(any(BigDecimal.class));
    }

    static Stream<Arguments> amountValues() {
        return Stream.of(
                null,
                Arguments.of(new BigDecimal("0.00")),
                Arguments.of(new BigDecimal("-1"))
        );
    }
}