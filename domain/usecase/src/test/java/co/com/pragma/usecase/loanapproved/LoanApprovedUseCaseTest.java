package co.com.pragma.usecase.loanapproved;

import co.com.pragma.model.loanreportevent.LoanReportEvent;
import co.com.pragma.model.loanreportevent.gateways.LoanReportEventRepository;
import co.com.pragma.model.loanreportevent.gateways.LoggerGateway;
import co.com.pragma.usecase.loanstats.LoanStatsUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApprovedUseCaseTest {

    private static final String STATUS_APPROVED = "APPROVED";

    @Mock
    private LoanReportEventRepository loanReportEventRepository;

    @Mock
    private LoanStatsUseCase loanStatsUseCase;

    @Mock
    private LoggerGateway logger;

    @InjectMocks
    private LoanApprovedUseCase loanApprovedUseCase;

    @Test
    void shouldSavedLoanReportEvent() {
        // Arrange
        LoanReportEvent loanReportEvent = loanReportEventMock(STATUS_APPROVED);

        // when reactive repositories
        when(loanReportEventRepository.save(any(LoanReportEvent.class))).thenReturn(Mono.just(loanReportEvent));
        when(loanStatsUseCase.increaseLoanStats(any(BigDecimal.class))).thenReturn(Mono.empty());

        // Assert
        StepVerifier.create(loanApprovedUseCase.processMessageApprovedLoanApplicationsReportQueue(loanReportEvent))
                .verifyComplete();

        Mockito.verify(loanReportEventRepository, times(1)).save(any(LoanReportEvent.class));
        Mockito.verify(loanStatsUseCase, times(1)).increaseLoanStats(any(BigDecimal.class));
    }

    @Test
    void shouldNotSaveLoanReportEventWhenErrorOccurred() {
        // Arrange
        LoanReportEvent loanReportEvent = loanReportEventMock(STATUS_APPROVED);

        // when reactive repositories
        when(loanReportEventRepository.save(any(LoanReportEvent.class))).thenReturn(Mono.just(loanReportEvent));
        when(loanStatsUseCase.increaseLoanStats(any(BigDecimal.class))).thenReturn(Mono.error(new RuntimeException("Simulated error")));

        // Assert
        StepVerifier.create(loanApprovedUseCase.processMessageApprovedLoanApplicationsReportQueue(loanReportEvent))
                .expectSubscription()
                .expectComplete()
                .verify();

        Mockito.verify(loanReportEventRepository, times(1)).save(any(LoanReportEvent.class));
        Mockito.verify(loanStatsUseCase, times(1)).increaseLoanStats(any(BigDecimal.class));
    }

    @ParameterizedTest
    @MethodSource("loanIdStatusAndAmount")
    void shouldSkipProcessingMessage(UUID loanId, String status, BigDecimal amount) {
        // Arrange
        LoanReportEvent loanReportEvent = LoanReportEvent.builder()
                .loanId(loanId)
                .documentNumber("123456789")
                .status(status)
                .amount(amount)
                .updatedAt(LocalDateTime.now())
                .build();

        //Assert
        StepVerifier.create(loanApprovedUseCase.processMessageApprovedLoanApplicationsReportQueue(loanReportEvent))
                .verifyComplete();

        Mockito.verify(loanReportEventRepository, never()).save(any(LoanReportEvent.class));
        Mockito.verify(loanStatsUseCase, never()).increaseLoanStats(any(BigDecimal.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING_REVIEW", "MANUAL_REVIEW", "REJECTED", "PENDING_VALIDATION"})
    void shouldSkipProcessingMessageWithStatusIsNotApproved(String status) {
        // Arrange
        LoanReportEvent loanReportEvent = loanReportEventMock(status);

        //Assert
        StepVerifier.create(loanApprovedUseCase.processMessageApprovedLoanApplicationsReportQueue(loanReportEvent))
                .verifyComplete();

        Mockito.verify(loanReportEventRepository, never()).save(any(LoanReportEvent.class));
        Mockito.verify(loanStatsUseCase, never()).increaseLoanStats(any(BigDecimal.class));
    }

    private LoanReportEvent loanReportEventMock(String status) {
        return LoanReportEvent.builder()
                .loanId(UUID.fromString("e8c49caa-e6ab-4e58-a0a9-e221bc152ec6"))
                .documentNumber("123456789")
                .status(status)
                .amount(new BigDecimal("250000.00"))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    static Stream<Arguments> loanIdStatusAndAmount() {
        return Stream.of(
                Arguments.of(null, STATUS_APPROVED, new BigDecimal("250000.00")),
                Arguments.of(UUID.fromString("e8c49caa-e6ab-4e58-a0a9-e221bc152ec6"), null, new BigDecimal("250000.00")),
                Arguments.of(UUID.fromString("e8c49caa-e6ab-4e58-a0a9-e221bc152ec6"), " ", new BigDecimal("250000.00")),
                Arguments.of(UUID.fromString("e8c49caa-e6ab-4e58-a0a9-e221bc152ec6"), STATUS_APPROVED, null),
                Arguments.of(UUID.fromString("e8c49caa-e6ab-4e58-a0a9-e221bc152ec6"), STATUS_APPROVED, new BigDecimal("-1"))
        );
    }
}